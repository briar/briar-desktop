package org.briarproject.briar.desktop.privategroups

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import org.briarproject.bramble.api.FormatException
import org.briarproject.bramble.api.contact.event.ContactRemovedEvent
import org.briarproject.bramble.api.db.DbException
import org.briarproject.bramble.api.db.NoSuchContactException
import org.briarproject.bramble.api.event.Event
import org.briarproject.bramble.api.event.EventBus
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
import org.briarproject.briar.api.privategroup.PrivateGroupManager
import org.briarproject.briar.desktop.utils.replaceIf
import org.briarproject.briar.desktop.viewmodel.BriarEventListenerViewModel
import java.util.Date
import java.util.logging.Level
import java.util.logging.Logger
import javax.inject.Inject

class ThreadedConversationViewModel
@Inject
constructor(
    private val privateGroupManager: PrivateGroupManager,
    private val conversationManager: ConversationManager,
    private val messagingManager: MessagingManager,
    private val privateMessageFactory: PrivateMessageFactory,
    private val eventBus: EventBus,
) : BriarEventListenerViewModel(eventBus) {

    companion object {
        private val LOG = Logger.getLogger(ThreadedConversationViewModel::class.java.name)
    }

    private val _privateGroupId = mutableStateOf<GroupId?>(null)
    private val _privateGroupItem = mutableStateOf<PrivateGroupItem?>(null)
    private val _messages = mutableStateListOf<ThreadedConversationItem>()

    private val _newMessage = mutableStateOf("")

    val contactItem: State<PrivateGroupItem?> = _privateGroupItem
    val messages: List<ThreadedConversationItem> = _messages

    val newMessage: State<String> = _newMessage

    fun setGroupId(id: GroupId) {
        if (_privateGroupId.value == id)
            return

        _privateGroupId.value = id
        _privateGroupItem.value = PrivateGroupItem(
            privateGroupManager.getPrivateGroup(id),
            privateGroupManager.getGroupCount(id),
        )
        loadMessages()
        setNewMessage("")
    }

    fun setNewMessage(msg: String) {
        _newMessage.value = msg
    }

    fun sendMessage() {
        try {
            val text = _newMessage.value
            _newMessage.value = ""

            // don't send empty or blank messages
            if (text.isBlank()) return

            val start = LogUtils.now()
            val m = createMessage(text)
            messagingManager.addLocalMessage(m)
            LogUtils.logDuration(LOG, "Storing message", start)

            val message = m.message
            val h = PrivateMessageHeader(
                message.id, message.groupId,
                message.timestamp, true, true, false, false,
                m.hasText(), m.attachmentHeaders,
                m.autoDeleteTimer
            )
            _messages.add(0, messageHeaderToItem(h))
            // eventBus.broadcast(ConversationMessageToBeSentEvent(h, _contactId.value!!))
        } catch (e: UnexpectedTimerException) {
            LogUtils.logException(LOG, Level.WARNING, e)
        } catch (e: DbException) {
            LogUtils.logException(LOG, Level.WARNING, e)
        }
    }

    @Throws(DbException::class)
    private fun createMessage(text: String): PrivateMessage {
        val groupId = _privateGroupItem.value!!.privateGroup.id
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
            val headers = privateGroupManager.getHeaders(_privateGroupId.value!!)
            LogUtils.logDuration(LOG, "Loading message headers", start)
            // Sort headers by timestamp in *descending* order
            val sorted = headers.sortedByDescending { it.timestamp }
            _messages.apply {
                clear()
                val start = LogUtils.now()
                addAll(
                    // todo: use ConversationVisitor to also display Request and Notice Messages
                    sorted.filterIsInstance<PrivateMessageHeader>().map(::messageHeaderToItem)
                )
                LogUtils.logDuration(LOG, "Loading messages", start)
            }
        } catch (e: NoSuchContactException) {
            LogUtils.logException(LOG, Level.WARNING, e)
        } catch (e: DbException) {
            LogUtils.logException(LOG, Level.WARNING, e)
        }
    }

    private fun messageHeaderToItem(h: PrivateMessageHeader): ThreadedConversationMessageItem {
        // todo: use ConversationVisitor instead and support other MessageHeader
        val item = ThreadedConversationMessageItem(h)
        if (h.hasText()) {
            item.text = loadMessageText(h.id)
        } else {
            LOG.warning { "private message without text" }
        }
        return item
    }

    private fun loadMessageText(m: MessageId): String? {
        try {
            return messagingManager.getMessageText(m)
        } catch (e: DbException) {
            LogUtils.logException(LOG, Level.WARNING, e)
        }
        return null
    }

    override fun eventOccurred(e: Event?) {
        when (e) {
            is ContactRemovedEvent -> {
                if (e.contactId == _privateGroupId.value) {
                    LOG.info("Contact removed")
                    // todo: we probably don't need to react to this here as the ContactsViewModel should already handle it
                }
            }
            is ConversationMessageReceivedEvent<*> -> {
                if (e.contactId == _privateGroupId.value) {
                    LOG.info("Message received, adding")
                    val h = e.messageHeader
                    if (h is PrivateMessageHeader) {
                        // insert at start of list according to descending sort order
                        _messages.add(0, messageHeaderToItem(h))
                    }
                }
            }
            is MessagesSentEvent -> {
                if (e.contactId == _privateGroupId.value) {
                    LOG.info("Messages sent")
                    markMessages(e.messageIds, sent = true, seen = false)
                }
            }
            is MessagesAckedEvent -> {
                if (e.contactId == _privateGroupId.value) {
                    LOG.info("Messages acked")
                    markMessages(e.messageIds, sent = true, seen = true)
                }
            }
            is ConversationMessagesDeletedEvent -> {
                if (e.contactId == _privateGroupId.value) {
                    LOG.info("Messages auto-deleted")
                    val messages = HashSet(e.messageIds)
                    _messages.removeIf { messages.contains(it.id) }
                }
            }
            /*
            is ContactConnectedEvent -> {
                if (e.contactId == _privateGroupId.value) {
                    LOG.info("Contact connected")
                    _privateGroupItem.value = _privateGroupItem.value!!.updateIsConnected(true)
                }
            }
            is ContactDisconnectedEvent -> {
                if (e.contactId == _privateGroupId.value) {
                    LOG.info("Contact disconnected")
                    _privateGroupItem.value = _privateGroupItem.value!!.updateIsConnected(false)
                }
            }
             */
            is ClientVersionUpdatedEvent -> {
                if (e.contactId == _privateGroupId.value) {
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
