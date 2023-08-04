/*
 * Briar Desktop
 * Copyright (C) 2021-2023 The Briar Project
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

package org.briarproject.briar.desktop.blog.sharing

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import mu.KotlinLogging
import org.briarproject.bramble.api.connection.ConnectionRegistry
import org.briarproject.bramble.api.contact.ContactManager
import org.briarproject.bramble.api.db.Transaction
import org.briarproject.bramble.api.db.TransactionManager
import org.briarproject.bramble.api.event.Event
import org.briarproject.bramble.api.event.EventBus
import org.briarproject.bramble.api.lifecycle.LifecycleManager
import org.briarproject.bramble.api.plugin.event.ContactConnectedEvent
import org.briarproject.bramble.api.plugin.event.ContactDisconnectedEvent
import org.briarproject.bramble.api.sync.GroupId
import org.briarproject.briar.api.blog.BlogManager
import org.briarproject.briar.api.blog.BlogSharingManager
import org.briarproject.briar.api.blog.event.BlogInvitationResponseReceivedEvent
import org.briarproject.briar.api.conversation.ConversationManager
import org.briarproject.briar.api.identity.AuthorManager
import org.briarproject.briar.api.sharing.SharingManager.SharingStatus.SHAREABLE
import org.briarproject.briar.api.sharing.SharingManager.SharingStatus.SHARING
import org.briarproject.briar.api.sharing.event.ContactLeftShareableEvent
import org.briarproject.briar.desktop.threadedgroup.sharing.InvitationSentEvent
import org.briarproject.briar.desktop.threadedgroup.sharing.ThreadedGroupSharingViewModel
import org.briarproject.briar.desktop.threading.BriarExecutors
import org.briarproject.briar.desktop.threading.UiExecutor
import org.briarproject.briar.desktop.viewmodel.asState
import org.briarproject.briar.desktop.viewmodel.update
import javax.inject.Inject

class BlogSharingViewModel @Inject constructor(
    private val blogManager: BlogManager,
    private val blogSharingManager: BlogSharingManager,
    contactManager: ContactManager,
    authorManager: AuthorManager,
    conversationManager: ConversationManager,
    private val connectionRegistry: ConnectionRegistry,
    briarExecutors: BriarExecutors,
    lifecycleManager: LifecycleManager,
    db: TransactionManager,
    eventBus: EventBus,
) : ThreadedGroupSharingViewModel(
    contactManager,
    authorManager,
    conversationManager,
    connectionRegistry,
    briarExecutors,
    lifecycleManager,
    db,
    eventBus,
) {

    companion object {
        private val LOG = KotlinLogging.logger {}
    }

    private val _contactName = mutableStateOf("")
    val contactName = _contactName.asState()

    val currentlySharedWith = derivedStateOf {
        _contactList.filter { _sharingStatus.value[it.id] == SHARING }
    }

    override fun reload() {
        _shareableSelected.value = emptySet()
        _sharingMessage.value = ""
        val groupId = _groupId ?: return
        runOnDbThreadWithTransaction(true) { txn ->
            val contactName = blogManager.getBlog(txn, groupId).author.name
            loadSharingStatus(txn, groupId)
            txn.attach { _contactName.value = contactName }
        }
    }

    @UiExecutor
    override fun shareThreadedGroup() = runOnDbThreadWithTransaction(false) { txn ->
        val groupId = _groupId ?: return@runOnDbThreadWithTransaction
        val message = _sharingMessage.value.ifEmpty { null }
        _shareableSelected.value.forEach { contactId ->
            blogSharingManager.sendInvitation(txn, groupId, contactId, message)
        }
        // send custom event to force message reload if private chat is open for contactId
        // todo: switch to a more generic approach where every locally sent message broadcasts an event per default
        txn.attach(InvitationSentEvent(_shareableSelected.value.toList()))
        txn.attach { reload() }
    }

    @UiExecutor
    override fun eventOccurred(e: Event) {
        super.eventOccurred(e)

        val groupId = _groupId ?: return
        when {
            e is BlogInvitationResponseReceivedEvent && e.messageHeader.shareableId == groupId -> {
                if (e.messageHeader.wasAccepted()) {
                    _sharingStatus.value += e.contactId to SHARING
                    val connected = connectionRegistry.isConnected(e.contactId)
                    _sharingInfo.update { addContact(connected) }
                } else {
                    _sharingStatus.value += e.contactId to SHAREABLE
                }
            }

            e is ContactLeftShareableEvent && e.groupId == groupId -> {
                _sharingStatus.value += e.contactId to SHAREABLE
                val connected = connectionRegistry.isConnected(e.contactId)
                _sharingInfo.update { removeContact(connected) }
            }

            e is ContactConnectedEvent -> {
                if (_sharingStatus.value[e.contactId] == SHARING)
                    _sharingInfo.update { updateContactConnected(true) }
            }

            e is ContactDisconnectedEvent -> {
                if (_sharingStatus.value[e.contactId] == SHARING)
                    _sharingInfo.update { updateContactConnected(false) }
            }
        }
    }

    private fun loadSharingStatus(txn: Transaction, groupId: GroupId) {
        val map = contactManager.getContacts(txn).associate { contact ->
            contact.id to blogSharingManager.getSharingStatus(txn, groupId, contact)
        }
        val sharing = map.filterValues { it == SHARING }.keys
        txn.attach {
            val online =
                sharing.fold(0) { acc, it -> if (connectionRegistry.isConnected(it)) acc + 1 else acc }
            _sharingStatus.value = map
            _sharingInfo.value = SharingInfo(sharing.size, online)
        }
    }
}
