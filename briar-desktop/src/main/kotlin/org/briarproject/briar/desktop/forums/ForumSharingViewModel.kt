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

package org.briarproject.briar.desktop.forums

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import mu.KotlinLogging
import org.briarproject.bramble.api.connection.ConnectionRegistry
import org.briarproject.bramble.api.contact.ContactManager
import org.briarproject.bramble.api.db.TransactionManager
import org.briarproject.bramble.api.event.Event
import org.briarproject.bramble.api.event.EventBus
import org.briarproject.bramble.api.lifecycle.LifecycleManager
import org.briarproject.bramble.api.plugin.event.ContactConnectedEvent
import org.briarproject.bramble.api.plugin.event.ContactDisconnectedEvent
import org.briarproject.bramble.api.sync.GroupId
import org.briarproject.briar.api.attachment.AttachmentReader
import org.briarproject.briar.api.conversation.ConversationManager
import org.briarproject.briar.api.forum.ForumSharingManager
import org.briarproject.briar.api.forum.event.ForumInvitationResponseReceivedEvent
import org.briarproject.briar.api.identity.AuthorManager
import org.briarproject.briar.api.sharing.event.ContactLeftShareableEvent
import org.briarproject.briar.desktop.contact.ContactItem
import org.briarproject.briar.desktop.threading.BriarExecutors
import org.briarproject.briar.desktop.threading.UiExecutor
import org.briarproject.briar.desktop.utils.ImageUtils.loadImage
import org.briarproject.briar.desktop.utils.clearAndAddAll
import org.briarproject.briar.desktop.utils.removeFirst
import org.briarproject.briar.desktop.utils.replaceFirst
import org.briarproject.briar.desktop.viewmodel.EventListenerDbViewModel
import org.briarproject.briar.desktop.viewmodel.asList
import org.briarproject.briar.desktop.viewmodel.asState
import org.briarproject.briar.desktop.viewmodel.update
import javax.inject.Inject

class ForumSharingViewModel @Inject constructor(
    private val forumSharingManager: ForumSharingManager,
    private val contactManager: ContactManager,
    private val authorManager: AuthorManager,
    private val conversationManager: ConversationManager,
    private val connectionRegistry: ConnectionRegistry,
    private val attachmentReader: AttachmentReader,
    briarExecutors: BriarExecutors,
    lifecycleManager: LifecycleManager,
    db: TransactionManager,
    eventBus: EventBus,
) : EventListenerDbViewModel(briarExecutors, lifecycleManager, db, eventBus) {

    companion object {
        private val LOG = KotlinLogging.logger {}
    }

    private lateinit var groupId: GroupId

    private val _currentlySharedWith = mutableStateListOf<ContactItem>()
    val currentlySharedWith = _currentlySharedWith.asList()

    private val _sharingInfo = mutableStateOf(SharingInfo(0, 0))
    val sharingInfo = _sharingInfo.asState()

    @UiExecutor
    fun setGroupId(groupId: GroupId) {
        if (this::groupId.isInitialized && groupId == this.groupId) return
        this.groupId = groupId
        loadSharedWith()
    }

    @UiExecutor
    override fun eventOccurred(e: Event) {
        when {
            e is ForumInvitationResponseReceivedEvent && e.messageHeader.shareableId == groupId && e.messageHeader.wasAccepted() ->
                runOnDbThreadWithTransaction(false) { txn ->
                    val contact = contactManager.getContact(txn, e.contactId)
                    val authorInfo = authorManager.getAuthorInfo(txn, contact)
                    val connected = connectionRegistry.isConnected(contact.id)
                    val item = ContactItem(
                        contact,
                        authorInfo,
                        connected,
                        conversationManager.getGroupCount(txn, contact.id), // todo: not necessary to be shown here
                        authorInfo.avatarHeader?.let { loadImage(txn, attachmentReader, it) },
                    )
                    txn.attach {
                        _currentlySharedWith.add(item)
                        _sharingInfo.update { addContact(connected) }
                    }
                }

            e is ContactLeftShareableEvent && e.groupId == groupId -> {
                _currentlySharedWith.removeFirst { it.idWrapper.contactId == e.contactId }
                val connected = connectionRegistry.isConnected(e.contactId)
                _sharingInfo.update { removeContact(connected) }
            }

            e is ContactConnectedEvent -> {
                val isMember = _currentlySharedWith.replaceFirst({ it.idWrapper.contactId == e.contactId }) {
                    it.updateIsConnected(true)
                }
                if (isMember) _sharingInfo.update { updateContactConnected(true) }
            }

            e is ContactDisconnectedEvent -> {
                val isMember = _currentlySharedWith.replaceFirst({ it.idWrapper.contactId == e.contactId }) {
                    it.updateIsConnected(false)
                }
                if (isMember) _sharingInfo.update { updateContactConnected(false) }
            }
        }
    }

    private fun loadSharedWith() = runOnDbThreadWithTransaction(true) { txn ->
        var online = 0
        val list = forumSharingManager.getSharedWith(txn, groupId).map { contact ->
            val authorInfo = authorManager.getAuthorInfo(txn, contact)
            val connected = connectionRegistry.isConnected(contact.id)
            if (connected) online++
            ContactItem(
                contact,
                authorInfo,
                connected,
                conversationManager.getGroupCount(txn, contact.id), // todo: not necessary to be shown here
                authorInfo.avatarHeader?.let { loadImage(txn, attachmentReader, it) },
            )
        }
        txn.attach {
            _currentlySharedWith.clearAndAddAll(list)
            _sharingInfo.value = SharingInfo(list.size, online)
        }
    }

    data class SharingInfo(val total: Int, val online: Int) {
        fun addContact(connected: Boolean) = copy(
            total = total + 1,
            online = if (connected) online + 1 else online
        )

        fun removeContact(connected: Boolean) = copy(
            total = total - 1,
            online = if (connected) online - 1 else online
        )

        fun updateContactConnected(connected: Boolean) = copy(
            total = total,
            online = if (connected) online + 1 else online - 1
        )
    }
}
