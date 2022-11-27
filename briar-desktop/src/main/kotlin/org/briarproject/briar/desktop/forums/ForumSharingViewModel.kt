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
    val currentlySharedWith: List<ContactItem> = _currentlySharedWith

    @UiExecutor
    fun setGroupId(groupId: GroupId) {
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
                    val item = ContactItem(
                        contact,
                        authorInfo,
                        connectionRegistry.isConnected(contact.id),
                        conversationManager.getGroupCount(txn, contact.id), // todo: not necessary to be shown here
                        authorInfo.avatarHeader?.let { loadImage(txn, attachmentReader, it) },
                    )
                    txn.attach { _currentlySharedWith.add(item) }
                }

            e is ContactLeftShareableEvent && e.groupId == groupId ->
                _currentlySharedWith.removeFirst { it.idWrapper.contactId == e.contactId }

            e is ContactConnectedEvent ->
                _currentlySharedWith.replaceFirst({ it.idWrapper.contactId == e.contactId }) { it.updateIsConnected(true) }

            e is ContactDisconnectedEvent ->
                _currentlySharedWith.replaceFirst({ it.idWrapper.contactId == e.contactId }) {
                    it.updateIsConnected(
                        false
                    )
                }
        }
    }

    private fun loadSharedWith() = runOnDbThreadWithTransaction(true) { txn ->
        val list = forumSharingManager.getSharedWith(txn, groupId).map { contact ->
            val authorInfo = authorManager.getAuthorInfo(txn, contact)
            ContactItem(
                contact,
                authorInfo,
                connectionRegistry.isConnected(contact.id),
                conversationManager.getGroupCount(txn, contact.id), // todo: not necessary to be shown here
                authorInfo.avatarHeader?.let { loadImage(txn, attachmentReader, it) },
            )
        }
        txn.attach {
            _currentlySharedWith.clearAndAddAll(list)
        }
    }
}
