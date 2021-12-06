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
import org.briarproject.briar.api.autodelete.UnexpectedTimerException
import org.briarproject.briar.api.autodelete.event.ConversationMessagesDeletedEvent
import org.briarproject.briar.api.conversation.ConversationManager
import org.briarproject.briar.api.conversation.event.ConversationMessageReceivedEvent
import org.briarproject.briar.api.messaging.MessagingManager
import org.briarproject.briar.api.messaging.PrivateMessage
import org.briarproject.briar.api.messaging.PrivateMessageFactory
import org.briarproject.briar.api.messaging.PrivateMessageHeader
import org.briarproject.briar.desktop.contact.ContactItem
import org.briarproject.briar.desktop.threading.BriarExecutors
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
    private val conversationManager: ConversationManager,
    private val messagingManager: MessagingManager,
    private val privateMessageFactory: PrivateMessageFactory,
    briarExecutors: BriarExecutors,
    lifecycleManager: LifecycleManager,
    db: TransactionManager,
    private val eventBus: EventBus,
) : EventListenerDbViewModel(briarExecutors, lifecycleManager, db, eventBus) {

    companion object {
        private val LOG = KotlinLogging.logger {}
    }

    private val _contactId = mutableStateOf<ContactId?>(null)
    private val _contactItem = mutableStateOf<ContactItem?>(null)
    private val _messages = mutableStateListOf<ConversationItem>()

    private val _newMessage = mutableStateOf("")

    val contactItem = _contactItem.asState()
    val messages = _messages.asList()

    val newMessage = _newMessage.asState()

    fun setContactId(id: ContactId) {
        if (_contactId.value == id)
            return

        _contactId.value = id
        _contactItem.value = null
        _messages.clear()

        loadContact(id)
        loadMessages(id)

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
                val msg = messageHeaderToItem(txn, h)
                txn.attach {
                    _messages.add(0, msg)
                }
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

    private fun loadContact(id: ContactId) = runOnDbThreadWithTransaction(true) { txn ->
        try {
            val start = LogUtils.now()
            val contactItem = ContactItem(
                contactManager.getContact(txn, id),
                connectionRegistry.isConnected(id),
                conversationManager.getGroupCount(txn, id),
            )
            LOG.logDuration("Loading contact", start)
            txn.attach { _contactItem.value = contactItem }
        } catch (e: NoSuchContactException) {
            // todo: handle this properly
            LOG.warn(e) {}
        }
    }

    private fun loadMessages(id: ContactId) = runOnDbThreadWithTransaction(true) { txn ->
        try {
            var start = LogUtils.now()
            val headers = conversationManager.getMessageHeaders(txn, id)
            LOG.logDuration("Loading message headers", start)
            // Sort headers by timestamp in *descending* order
            val sorted = headers.sortedByDescending { it.timestamp }
            // todo: use ConversationVisitor to also display Request and Notice Messages
            start = LogUtils.now()
            val messages = sorted.filterIsInstance<PrivateMessageHeader>().map { messageHeaderToItem(txn, it) }
            LOG.logDuration("Loading messages", start)
            txn.attach { _messages.clearAndAddAll(messages) }
        } catch (e: NoSuchContactException) {
            // todo: handle this properly
            LOG.warn(e) {}
        }
    }

    private fun messageHeaderToItem(txn: Transaction, h: PrivateMessageHeader): ConversationMessageItem {
        // todo: use ConversationVisitor instead and support other MessageHeader
        val item = ConversationMessageItem(h)
        if (h.hasText()) {
            item.text = loadMessageText(txn, h.id)
        } else {
            LOG.warn { "private message without text" }
        }
        return item
    }

    private fun loadMessageText(txn: Transaction, m: MessageId): String? {
        try {
            return messagingManager.getMessageText(txn, m)
        } catch (e: DbException) {
            LOG.warn(e) {}
        }
        return null
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
                    if (h is PrivateMessageHeader) {
                        // insert at start of list according to descending sort order
                        runOnDbThreadWithTransaction(true) { txn ->
                            val msg = messageHeaderToItem(txn, h)
                            txn.attach { _messages.add(0, msg) }
                        }
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
}
