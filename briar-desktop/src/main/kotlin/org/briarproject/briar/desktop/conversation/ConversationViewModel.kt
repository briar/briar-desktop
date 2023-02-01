/*
 * Briar Desktop
 * Copyright (C) 2021-2022 The Briar Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.briarproject.briar.desktop.conversation

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toAwtImage
import mu.KotlinLogging
import org.briarproject.bramble.api.FormatException
import org.briarproject.bramble.api.connection.ConnectionRegistry
import org.briarproject.bramble.api.contact.ContactId
import org.briarproject.bramble.api.contact.ContactManager
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
import org.briarproject.briar.api.attachment.AttachmentHeader
import org.briarproject.briar.api.attachment.AttachmentReader
import org.briarproject.briar.api.autodelete.UnexpectedTimerException
import org.briarproject.briar.api.autodelete.event.ConversationMessagesDeletedEvent
import org.briarproject.briar.api.conversation.ConversationManager
import org.briarproject.briar.api.conversation.DeletionResult
import org.briarproject.briar.api.conversation.event.ConversationMessageReceivedEvent
import org.briarproject.briar.api.forum.ForumSharingManager
import org.briarproject.briar.api.identity.AuthorManager
import org.briarproject.briar.api.introduction.IntroductionManager
import org.briarproject.briar.api.messaging.MessagingConstants.MAX_PRIVATE_MESSAGE_TEXT_LENGTH
import org.briarproject.briar.api.messaging.MessagingManager
import org.briarproject.briar.api.messaging.PrivateMessage
import org.briarproject.briar.api.messaging.PrivateMessageFactory
import org.briarproject.briar.api.messaging.PrivateMessageHeader
import org.briarproject.briar.desktop.DesktopFeatureFlags
import org.briarproject.briar.desktop.attachment.media.ImageCompressor
import org.briarproject.briar.desktop.contact.ContactItem
import org.briarproject.briar.desktop.contact.loadContactItem
import org.briarproject.briar.desktop.conversation.ConversationRequestItem.RequestType.FORUM
import org.briarproject.briar.desktop.conversation.ConversationRequestItem.RequestType.INTRODUCTION
import org.briarproject.briar.desktop.threading.BriarExecutors
import org.briarproject.briar.desktop.utils.KLoggerUtils.e
import org.briarproject.briar.desktop.utils.KLoggerUtils.i
import org.briarproject.briar.desktop.utils.KLoggerUtils.logDuration
import org.briarproject.briar.desktop.utils.KLoggerUtils.w
import org.briarproject.briar.desktop.utils.StringUtils.takeUtf8
import org.briarproject.briar.desktop.utils.addAfterLast
import org.briarproject.briar.desktop.utils.clearAndAddAll
import org.briarproject.briar.desktop.utils.replaceIf
import org.briarproject.briar.desktop.utils.replaceIfIndexed
import org.briarproject.briar.desktop.viewmodel.EventListenerDbViewModel
import org.briarproject.briar.desktop.viewmodel.SingleStateEvent
import org.briarproject.briar.desktop.viewmodel.asList
import org.briarproject.briar.desktop.viewmodel.asState
import org.briarproject.briar.desktop.viewmodel.update
import javax.inject.Inject
import kotlin.concurrent.thread

class ConversationViewModel
@Inject
constructor(
    private val connectionRegistry: ConnectionRegistry,
    private val contactManager: ContactManager,
    private val authorManager: AuthorManager,
    private val conversationManager: ConversationManager,
    private val introductionManager: IntroductionManager,
    private val forumSharingManager: ForumSharingManager,
    private val messagingManager: MessagingManager,
    private val privateMessageFactory: PrivateMessageFactory,
    briarExecutors: BriarExecutors,
    lifecycleManager: LifecycleManager,
    db: TransactionManager,
    private val attachmentReader: AttachmentReader,
    private val imageCompressor: ImageCompressor,
    private val desktopFeatureFlags: DesktopFeatureFlags,
    private val eventBus: EventBus,
) : EventListenerDbViewModel(briarExecutors, lifecycleManager, db, eventBus) {

    companion object {
        private val LOG = KotlinLogging.logger {}
    }

    private val _contactId = mutableStateOf<ContactId?>(null)
    private val _contactItem = mutableStateOf<ContactItem?>(null)
    private val _messages = mutableStateListOf<ConversationItem>()
    private val _loadingMessages = mutableStateOf(false)

    private val _newMessageImage = mutableStateOf<ImageBitmap?>(null)
    private val _newMessage = mutableStateOf("")

    private val _deletionResult = mutableStateOf<DeletionResult?>(null)

    val contactItem = _contactItem.asState()
    val messages = _messages.asList()
    val loadingMessages = _loadingMessages.asState()

    val newMessageImage = _newMessageImage.asState()
    val newMessage = _newMessage.asState()

    val deletionResult = _deletionResult.asState()

    // for update alias dialog
    private val _newAlias = mutableStateOf("")
    val newAlias = _newAlias.asState()

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
        setNewMessageImage(null)
    }

    fun setNewMessage(msg: String) {
        _newMessage.value = msg.takeUtf8(MAX_PRIVATE_MESSAGE_TEXT_LENGTH)
    }

    fun setNewMessageImage(image: ImageBitmap?) {
        _newMessageImage.value = image
    }

    fun setNewAlias(alias: String) {
        _newAlias.value = alias
    }

    fun sendMessage() {
        val text = _newMessage.value
        val image = _newMessageImage.value

        val trimmed = text.trim()

        // don't send empty or blank messages
        if (trimmed.isBlank() && image == null) return

        _newMessage.value = ""
        _newMessageImage.value = null

        val contactId = _contactId.value!!

        // TODO: unfortunately, there is no transactional version of addLocalAttachment yet,
        //  so I need to create the attachment headers outside of the transaction that is used to adding the
        //  actual message.
        //  It's done similarly on Android: https://code.briarproject.org/briar/briar/-/blob/master/briar-android/src/main/java/org/briarproject/briar/android/attachment/AttachmentCreationTask.java#L95

        // Offload to a separate thread in order not to block the UI while waiting for the images to be loaded
        // and added to the database.
        thread {
            // First: get the group id and add images if any
            val groupId = messagingManager.getConversationId(contactId)
            val headers = if (image == null) emptyList() else buildList {
                val timestamp = System.currentTimeMillis()
                val compressed = imageCompressor.compressImage(image.toAwtImage())
                add(messagingManager.addLocalAttachment(groupId, timestamp, "image/jpeg", compressed))
            }

            // Second: add the actual message to the database
            runOnDbThreadWithTransaction(false) { txn ->
                try {
                    val start = LogUtils.now()
                    val m = createMessage(txn, contactId, groupId, trimmed.ifBlank { null }, headers)
                    messagingManager.addLocalMessage(txn, m)
                    LOG.logDuration("Storing message", start)

                    val message = m.message
                    val h = PrivateMessageHeader(
                        message.id, message.groupId,
                        message.timestamp, true, true, false, false,
                        m.hasText(), m.attachmentHeaders,
                        m.autoDeleteTimer
                    )
                    val visitor =
                        ConversationVisitor(
                            contactItem.value!!.displayName,
                            messagingManager,
                            attachmentReader,
                            desktopFeatureFlags,
                            txn
                        )
                    val msg = h.accept(visitor)!!
                    txn.attach { addMessage(msg) }
                } catch (e: UnexpectedTimerException) {
                    // todo: handle this properly
                    LOG.w(e) {}
                } catch (e: DbException) {
                    // todo: handle this properly
                    LOG.w(e) {}
                }
            }
        }
    }

    // first unread message when first opening the list
    // used to draw a horizontal divider on that position as long as list is opened
    // we cannot use [derivedStateOf] here as it would move the line after first showing the list
    private val _initialFirstUnreadMessageIndex = mutableStateOf(-1)
    val initialFirstUnreadMessageIndex = _initialFirstUnreadMessageIndex.asState()

    val currentUnreadMessagesInfo = derivedStateOf {
        UnreadMessagesInfo(
            amount = _messages.count { !it.isRead },
            firstIndex = _messages.indexOfFirst { !it.isRead },
            lastIndex = _messages.indexOfLast { !it.isRead }
        )
    }

    data class UnreadMessagesInfo(
        val amount: Int,
        val firstIndex: Int,
        val lastIndex: Int,
    )

    val onMessageAddedToBottom = SingleStateEvent<MessageAddedType>()

    enum class MessageAddedType { OUTGOING, INCOMING }

    fun markMessagesRead(indices: List<Int>) {
        val id = _contactId.value!!
        val messages = _messages.toList()
        runOnDbThreadWithTransaction(false) { txn ->
            var count = 0
            messages.filterIndexed { idx, it -> idx in indices && !it.isRead }.forEach {
                conversationManager.setReadFlag(txn, it.groupId, it.id, true)
                count++
            }
            txn.attach {
                _messages.replaceIfIndexed({ idx, it -> idx in indices && !it.isRead }) { _, it ->
                    it.markRead()
                }
            }
            if (count > 0) eventBus.broadcast(ConversationMessagesReadEvent(count, id))
        }
    }

    @Throws(DbException::class)
    private fun createMessage(
        txn: Transaction,
        contactId: ContactId,
        groupId: GroupId,
        text: String?,
        headers: List<AttachmentHeader>,
    ): PrivateMessage {
        val timestamp = conversationManager.getTimestampForOutgoingMessage(txn, contactId)
        try {
            return privateMessageFactory.createPrivateMessage(groupId, timestamp, text, headers)
        } catch (e: FormatException) {
            throw AssertionError(e)
        }
    }

    private fun loadContact(txn: Transaction, id: ContactId): ContactItem {
        try {
            val start = LogUtils.now()

            val contact = contactManager.getContact(txn, id)
            val contactItem =
                loadContactItem(txn, contact, authorManager, connectionRegistry, conversationManager)
            LOG.logDuration("Loading contact", start)
            txn.attach {
                _contactItem.value = contactItem
                _newAlias.value = contactItem.alias ?: ""
            }
            return contactItem
        } catch (e: NoSuchContactException) {
            // todo: handle this properly
            LOG.w(e) {}
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
            val headers = conversationManager.getMessageHeaders(txn, contact.id)
            LOG.logDuration("Loading message headers", start)
            // Sort headers by timestamp in *ascending* order
            val sorted = headers.sortedBy { it.timestamp }
            start = LogUtils.now()
            val visitor =
                ConversationVisitor(contact.displayName, messagingManager, attachmentReader, desktopFeatureFlags, txn)
            val messages = sorted.map { h -> h.accept(visitor)!! }
            LOG.logDuration("Loading messages", start)
            txn.attach {
                _messages.clearAndAddAll(messages)
                _initialFirstUnreadMessageIndex.value = messages.indexOfFirst { !it.isRead }
            }
        } catch (e: NoSuchContactException) {
            // todo: handle this properly
            LOG.w(e) {}
        } finally {
            _loadingMessages.value = false
        }
    }

    override fun eventOccurred(e: Event) {
        when (e) {
            is ConversationMessageReceivedEvent<*> -> {
                if (e.contactId == _contactId.value) {
                    LOG.i { "Message received, adding" }
                    val h = e.messageHeader
                    // insert at start of list according to descending sort order
                    runOnDbThreadWithTransaction(true) { txn ->
                        val visitor =
                            ConversationVisitor(
                                contactItem.value!!.displayName,
                                messagingManager,
                                attachmentReader,
                                desktopFeatureFlags,
                                txn
                            )
                        val msg = h.accept(visitor)!!
                        txn.attach { addMessage(msg) }
                    }
                }
            }

            is MessagesSentEvent -> {
                if (e.contactId == _contactId.value) {
                    LOG.i { "Messages sent" }
                    markMessages(e.messageIds, sent = true, seen = false)
                }
            }

            is MessagesAckedEvent -> {
                if (e.contactId == _contactId.value) {
                    LOG.i { "Messages acked" }
                    markMessages(e.messageIds, sent = true, seen = true)
                }
            }

            is ConversationMessagesDeletedEvent -> {
                if (e.contactId == _contactId.value) {
                    LOG.i { "Messages auto-deleted" }
                    val messages = HashSet(e.messageIds)
                    _messages.removeIf { messages.contains(it.id) }
                }
            }

            is ContactConnectedEvent -> {
                if (e.contactId == _contactId.value) {
                    LOG.i { "Contact connected" }
                    _contactItem.update { this?.updateIsConnected(true) }
                }
            }

            is ContactDisconnectedEvent -> {
                if (e.contactId == _contactId.value) {
                    LOG.i { "Contact disconnected" }
                    _contactItem.update { this?.updateIsConnected(false) }
                }
            }

            is ClientVersionUpdatedEvent -> {
                if (e.contactId == _contactId.value) {
                    // todo: still not implemented
                }
            }
        }
    }

    private fun addMessage(msg: ConversationItem) {
        val idx = _messages.addAfterLast(msg) { it.time < msg.time }
        if (idx == _messages.lastIndex) {
            // only emit the event in case the message was actually added to the bottom
            val type = if (msg.isIncoming) MessageAddedType.INCOMING else MessageAddedType.OUTGOING
            onMessageAddedToBottom.emit(type)
        }
    }

    private fun markMessages(
        messageIds: Collection<MessageId>,
        sent: Boolean,
        seen: Boolean,
    ) {
        val messages = HashSet(messageIds)
        _messages.replaceIf({ it.isOutgoing && messages.contains(it.id) }) {
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

                FORUM -> {
                    if (desktopFeatureFlags.shouldEnableForums()) {
                        forumSharingManager.respondToInvitation(
                            /* txn = */ txn,
                            /* c = */ _contactId.value!!,
                            /* id = */ item.sessionId,
                            /* accept = */ accept
                        )
                    } else {
                        LOG.e { "Forum requests are not supported for this build." }
                    }
                }

                else ->
                    LOG.e { "Only introduction and forum requests are supported for the time being." }
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

    fun changeAlias() = runOnDbThread {
        val newAlias = _newAlias.value.ifBlank { null }
        if (_contactId.value != null && contactItem.value != null) {
            contactManager.setContactAlias(_contactId.value!!, newAlias)
            _contactItem.update { this?.updateAlias(newAlias) }
        }
    }

    fun resetAlias() {
        _newAlias.value = contactItem.value?.alias ?: ""
    }

    fun deleteContact() = runOnDbThread {
        contactManager.removeContact(_contactId.value!!)
    }
}
