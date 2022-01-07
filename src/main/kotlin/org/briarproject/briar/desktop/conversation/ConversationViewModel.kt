package org.briarproject.briar.desktop.conversation

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import mu.KotlinLogging
import org.briarproject.bramble.api.FormatException
import org.briarproject.bramble.api.connection.ConnectionRegistry
import org.briarproject.bramble.api.contact.ContactId
import org.briarproject.bramble.api.contact.ContactManager
import org.briarproject.bramble.api.contact.event.ContactRemovedEvent
import org.briarproject.bramble.api.db.DbException
import org.briarproject.bramble.api.db.NoSuchContactException
import org.briarproject.bramble.api.db.Transaction
import org.briarproject.bramble.api.db.TransactionManager
import org.briarproject.bramble.api.event.Event
import org.briarproject.bramble.api.event.EventBus
import org.briarproject.bramble.api.lifecycle.LifecycleManager
import org.briarproject.bramble.api.plugin.event.ContactConnectedEvent
import org.briarproject.bramble.api.plugin.event.ContactDisconnectedEvent
import org.briarproject.bramble.api.sync.GroupId
import org.briarproject.bramble.api.sync.MessageId
import org.briarproject.bramble.api.sync.event.MessagesAckedEvent
import org.briarproject.bramble.api.sync.event.MessagesSentEvent
import org.briarproject.bramble.api.versioning.event.ClientVersionUpdatedEvent
import org.briarproject.bramble.util.LogUtils
import org.briarproject.briar.api.attachment.AttachmentReader
import org.briarproject.briar.api.autodelete.UnexpectedTimerException
import org.briarproject.briar.api.autodelete.event.ConversationMessagesDeletedEvent
import org.briarproject.briar.api.conversation.ConversationManager
import org.briarproject.briar.api.conversation.DeletionResult
import org.briarproject.briar.api.conversation.event.ConversationMessageReceivedEvent
import org.briarproject.briar.api.identity.AuthorManager
import org.briarproject.briar.api.introduction.IntroductionManager
import org.briarproject.briar.api.messaging.MessagingManager
import org.briarproject.briar.api.messaging.PrivateMessage
import org.briarproject.briar.api.messaging.PrivateMessageFactory
import org.briarproject.briar.api.messaging.PrivateMessageHeader
import org.briarproject.briar.desktop.contact.ContactItem
import org.briarproject.briar.desktop.conversation.ConversationRequestItem.RequestType.INTRODUCTION
import org.briarproject.briar.desktop.threading.BriarExecutors
import org.briarproject.briar.desktop.utils.ImageUtils.loadAvatar
import org.briarproject.briar.desktop.utils.KLoggerUtils.logDuration
import org.briarproject.briar.desktop.utils.clearAndAddAll
import org.briarproject.briar.desktop.utils.replaceIf
import org.briarproject.briar.desktop.utils.replaceIfIndexed
import org.briarproject.briar.desktop.viewmodel.EventListenerDbViewModel
import org.briarproject.briar.desktop.viewmodel.asList
import org.briarproject.briar.desktop.viewmodel.asState
import javax.inject.Inject

class ConversationViewModel
@Inject
constructor(
    private val connectionRegistry: ConnectionRegistry,
    private val contactManager: ContactManager,
    private val authorManager: AuthorManager,
    private val conversationManager: ConversationManager,
    private val introductionManager: IntroductionManager,
    private val messagingManager: MessagingManager,
    private val privateMessageFactory: PrivateMessageFactory,
    briarExecutors: BriarExecutors,
    lifecycleManager: LifecycleManager,
    db: TransactionManager,
    private val attachmentReader: AttachmentReader,
    private val eventBus: EventBus,
) : EventListenerDbViewModel(briarExecutors, lifecycleManager, db, eventBus) {

    companion object {
        private val LOG = KotlinLogging.logger {}
    }

    private val _contactId = mutableStateOf<ContactId?>(null)
    private val _contactItem = mutableStateOf<ContactItem?>(null)
    private val _messages = mutableStateListOf<ConversationItem>()
    private val _loadingMessages = mutableStateOf(false)

    private val _newMessage = mutableStateOf("")

    private val _deletionResult = mutableStateOf<DeletionResult?>(null)

    val contactItem = _contactItem.asState()
    val messages = _messages.asList()
    val loadingMessages = _loadingMessages.asState()

    val newMessage = _newMessage.asState()

    val deletionResult = _deletionResult.asState()

    fun setContactId(id: ContactId) {
        if (_contactId.value == id)
            return

        _contactId.value = id
        _contactItem.value = null
        _messages.clear()

        runOnDbThreadWithTransaction(true) { txn ->
            val contact = loadContact(txn, id)
            loadMessages(txn, contact)
        }

        setNewMessage("")
    }

    fun setNewMessage(msg: String) {
        _newMessage.value = msg
    }

    fun sendMessage() {
        val text = _newMessage.value

        // don't send empty or blank messages
        if (text.isBlank()) return

        _newMessage.value = ""

        val contactId = _contactId.value!!
        runOnDbThreadWithTransaction(false) { txn ->
            try {
                val start = LogUtils.now()
                val groupId = messagingManager.getConversationId(txn, contactId)
                val m = createMessage(txn, contactId, groupId, text)
                messagingManager.addLocalMessage(txn, m)
                LOG.logDuration("Storing message", start)

                val message = m.message
                val h = PrivateMessageHeader(
                    message.id, message.groupId,
                    message.timestamp, true, true, false, false,
                    m.hasText(), m.attachmentHeaders,
                    m.autoDeleteTimer
                )
                val visitor = ConversationVisitor(contactItem.value!!.name, messagingManager, txn)
                val msg = h.accept(visitor)!!
                txn.attach { _messages.add(0, msg) }
            } catch (e: UnexpectedTimerException) {
                // todo: handle this properly
                LOG.warn(e) {}
            } catch (e: DbException) {
                // todo: handle this properly
                LOG.warn(e) {}
            }
        }
    }

    val hasUnreadMessages = derivedStateOf { _messages.any { !it.isRead } }

    fun markMessagesRead(untilIndex: Int) {
        val id = _contactId.value!!
        val messages = _messages.toList()
        runOnDbThreadWithTransaction(false) { txn ->
            var count = 0
            messages.filterIndexed { idx, it -> idx >= untilIndex && !it.isRead }.forEach {
                conversationManager.setReadFlag(txn, it.groupId, it.id, true)
                count++
            }
            txn.attach {
                _messages.replaceIfIndexed({ idx, it -> idx >= untilIndex && !it.isRead }) { _, it ->
                    it.markRead()
                }
            }
            if (count > 0) eventBus.broadcast(ConversationMessagesReadEvent(count, id))
        }
    }

    @Throws(DbException::class)
    private fun createMessage(txn: Transaction, contactId: ContactId, groupId: GroupId, text: String): PrivateMessage {
        val timestamp = conversationManager.getTimestampForOutgoingMessage(txn, contactId)
        try {
            return privateMessageFactory.createLegacyPrivateMessage(
                groupId, timestamp, text
            )
        } catch (e: FormatException) {
            throw AssertionError(e)
        }
    }

    private fun loadContact(txn: Transaction, id: ContactId): ContactItem {
        try {
            val start = LogUtils.now()

            val contact = contactManager.getContact(txn, id)

            val contactItem = ContactItem(
                contact,
                connectionRegistry.isConnected(id),
                conversationManager.getGroupCount(txn, id),
                loadAvatar(authorManager, attachmentReader, txn, contact),
            )
            LOG.logDuration("Loading contact", start)
            txn.attach { _contactItem.value = contactItem }
            return contactItem
        } catch (e: NoSuchContactException) {
            // todo: handle this properly
            LOG.warn(e) {}
            throw e
        }
    }

    fun reloadMessages() = runOnDbThreadWithTransaction(true) { txn ->
        loadMessages(txn, contactItem.value!!)
    }

    private fun loadMessages(txn: Transaction, contact: ContactItem) {
        _loadingMessages.value = true
        try {
            var start = LogUtils.now()
            val headers = conversationManager.getMessageHeaders(txn, contact.idWrapper.contactId)
            LOG.logDuration("Loading message headers", start)
            // Sort headers by timestamp in *descending* order
            val sorted = headers.sortedByDescending { it.timestamp }
            start = LogUtils.now()
            val visitor = ConversationVisitor(contact.name, messagingManager, txn)
            val messages = sorted.map { h -> h.accept(visitor)!! }
            LOG.logDuration("Loading messages", start)
            txn.attach { _messages.clearAndAddAll(messages) }
        } catch (e: NoSuchContactException) {
            // todo: handle this properly
            LOG.warn(e) {}
        } finally {
            _loadingMessages.value = false
        }
    }

    override fun eventOccurred(e: Event?) {
        when (e) {
            is ContactRemovedEvent -> {
                if (e.contactId == _contactId.value) {
                    LOG.info("Contact removed")
                    // todo: we probably don't need to react to this here as the ContactsViewModel should already handle it
                }
            }
            is ConversationMessageReceivedEvent<*> -> {
                if (e.contactId == _contactId.value) {
                    LOG.info("Message received, adding")
                    val h = e.messageHeader
                    // insert at start of list according to descending sort order
                    runOnDbThreadWithTransaction(true) { txn ->
                        val visitor = ConversationVisitor(contactItem.value!!.name, messagingManager, txn)
                        val msg = h.accept(visitor)!!
                        txn.attach { _messages.add(0, msg) }
                    }
                }
            }
            is MessagesSentEvent -> {
                if (e.contactId == _contactId.value) {
                    LOG.info("Messages sent")
                    markMessages(e.messageIds, sent = true, seen = false)
                }
            }
            is MessagesAckedEvent -> {
                if (e.contactId == _contactId.value) {
                    LOG.info("Messages acked")
                    markMessages(e.messageIds, sent = true, seen = true)
                }
            }
            is ConversationMessagesDeletedEvent -> {
                if (e.contactId == _contactId.value) {
                    LOG.info("Messages auto-deleted")
                    val messages = HashSet(e.messageIds)
                    _messages.removeIf { messages.contains(it.id) }
                }
            }
            is ContactConnectedEvent -> {
                if (e.contactId == _contactId.value) {
                    LOG.info("Contact connected")
                    _contactItem.value = _contactItem.value!!.updateIsConnected(true)
                }
            }
            is ContactDisconnectedEvent -> {
                if (e.contactId == _contactId.value) {
                    LOG.info("Contact disconnected")
                    _contactItem.value = _contactItem.value!!.updateIsConnected(false)
                }
            }
            is ClientVersionUpdatedEvent -> {
                if (e.contactId == _contactId.value) {
                    // todo: still not implemented
                }
            }
        }
    }

    private fun markMessages(
        messageIds: Collection<MessageId>,
        sent: Boolean,
        seen: Boolean
    ) {
        val messages = HashSet(messageIds)
        _messages.replaceIf({ !it.isIncoming && messages.contains(it.id) }) {
            it.mark(sent, seen)
        }
    }

    fun respondToRequest(item: ConversationRequestItem, accept: Boolean) {
        _messages.replaceIf({ item == it }) {
            item.markAnswered()
        }
        runOnDbThreadWithTransaction(false) { txn ->
            when (item.requestType) {
                INTRODUCTION ->
                    introductionManager.respondToIntroduction(txn, _contactId.value!!, item.sessionId, accept)
                else ->
                    throw IllegalArgumentException("Only introduction requests are supported for the time being.")
            }
            // reload all messages to also show request response message
            // todo: might be better to have an event to react to, also for (other) outgoing messages
            loadMessages(txn, contactItem.value!!)
        }
    }

    fun deleteMessage(id: MessageId) = runOnDbThread {
        val result = conversationManager.deleteMessages(_contactId.value!!, listOf(id))
        if (result.allDeleted()) {
            _messages.removeIf { it.id == id }
        } else {
            _deletionResult.value = result
        }
    }

    fun deleteAllMessages() = runOnDbThread {
        _loadingMessages.value = true
        try {
            val result = conversationManager.deleteAllMessages(_contactId.value!!)
            reloadConversationAfterDeletingMessages(result)
        } finally {
            _loadingMessages.value = false
        }
    }

    private fun reloadConversationAfterDeletingMessages(result: DeletionResult) {
        reloadMessages()
        _deletionResult.value = if (!result.allDeleted()) result else null
    }

    fun confirmDeletionResult() {
        _deletionResult.value = null
    }
}
