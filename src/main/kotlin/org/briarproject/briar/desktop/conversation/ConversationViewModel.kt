package org.briarproject.briar.desktop.conversation

import androidx.compose.runtime.State
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
import org.briarproject.bramble.api.event.Event
import org.briarproject.bramble.api.event.EventBus
import org.briarproject.bramble.api.event.EventListener
import org.briarproject.bramble.api.plugin.event.ContactConnectedEvent
import org.briarproject.bramble.api.plugin.event.ContactDisconnectedEvent
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
import org.briarproject.briar.desktop.utils.KLoggerUtils.logDuration
import org.briarproject.briar.desktop.utils.replaceIf
import java.util.Date
import javax.inject.Inject

class ConversationViewModel
@Inject
constructor(
    private val connectionRegistry: ConnectionRegistry,
    private val contactManager: ContactManager,
    private val conversationManager: ConversationManager,
    private val messagingManager: MessagingManager,
    private val eventBus: EventBus,
    private val privateMessageFactory: PrivateMessageFactory,
) : EventListener {

    companion object {
        private val LOG = KotlinLogging.logger {}
    }

    init {
        // todo: where/when to remove listener again?
        eventBus.addListener(this)
    }

    private val _contactId = mutableStateOf<ContactId?>(null)
    private val _contactItem = mutableStateOf<ContactItem?>(null)
    private val _messages = mutableStateListOf<ConversationItem>()

    private val _newMessage = mutableStateOf("")

    val contactItem: State<ContactItem?> = _contactItem
    val messages: List<ConversationItem> = _messages

    val newMessage: State<String> = _newMessage

    fun setContactId(id: ContactId) {
        _contactId.value = id
        _contactItem.value = ContactItem(
            contactManager.getContact(id),
            connectionRegistry.isConnected(id),
            conversationManager.getGroupCount(id),
        )
        loadMessages()
    }

    fun setNewMessage(msg: String) {
        _newMessage.value = msg
    }

    fun sendMessage() {
        try {
            val text = _newMessage.value
            _newMessage.value = ""

            val substituted = text.replace("\r", "\\r")
                .replace("\n", "\\n")
            println("Sending: '$substituted'")

            // don't send empty or blank messages
            if (text.isBlank()) return

            val start = LogUtils.now()
            val m = createMessage(text)
            messagingManager.addLocalMessage(m)
            LOG.logDuration("Storing message", start)

            val message = m.message
            val h = PrivateMessageHeader(
                message.id, message.groupId,
                message.timestamp, true, true, false, false,
                m.hasText(), m.attachmentHeaders,
                m.autoDeleteTimer
            )
            _messages.add(0, messageHeaderToItem(h))
            eventBus.broadcast(ConversationMessageToBeSentEvent(h, _contactId.value!!))
        } catch (e: UnexpectedTimerException) {
            LOG.warn(e) {}
        } catch (e: DbException) {
            LOG.warn(e) {}
        }
    }

    @Throws(DbException::class)
    private fun createMessage(text: String): PrivateMessage {
        val groupId = messagingManager.getContactGroup(_contactItem.value!!.contact).id
        // todo: this API call needs a database transaction context
        // val timestamp = conversationManager.getTimestampForOutgoingMessage(_contactId.value!!)
        val timestamp = Date().time
        try {
            return privateMessageFactory.createLegacyPrivateMessage(
                groupId, timestamp, text
            )
        } catch (e: FormatException) {
            throw AssertionError(e)
        }
    }

    private fun loadMessages() {
        try {
            val start = LogUtils.now()
            val headers = conversationManager.getMessageHeaders(_contactId.value!!)
            LOG.logDuration("Loading message headers", start)
            // Sort headers by timestamp in *descending* order
            val sorted = headers.sortedByDescending { it.timestamp }
            _messages.apply {
                clear()
                val start = LogUtils.now()
                addAll(
                    // todo: use ConversationVisitor to also display Request and Notice Messages
                    sorted.filterIsInstance<PrivateMessageHeader>().map(::messageHeaderToItem)
                )
                LOG.logDuration("Loading messages", start)
            }
        } catch (e: NoSuchContactException) {
            LOG.warn(e) {}
        } catch (e: DbException) {
            LOG.warn(e) {}
        }
    }

    private fun messageHeaderToItem(h: PrivateMessageHeader): ConversationMessageItem {
        // todo: use ConversationVisitor instead and support other MessageHeader
        val item = ConversationMessageItem(h)
        if (h.hasText()) {
            item.text = loadMessageText(h.id)
        } else {
            LOG.warn { "private message without text" }
        }
        return item
    }

    private fun loadMessageText(m: MessageId): String? {
        try {
            return messagingManager.getMessageText(m)
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
                        _messages.add(0, messageHeaderToItem(h))
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
