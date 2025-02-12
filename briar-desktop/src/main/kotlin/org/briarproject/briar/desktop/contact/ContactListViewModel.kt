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

package org.briarproject.briar.desktop.contact

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import io.github.oshai.kotlinlogging.KotlinLogging
import org.briarproject.bramble.api.connection.ConnectionRegistry
import org.briarproject.bramble.api.contact.ContactManager
import org.briarproject.bramble.api.contact.PendingContactId
import org.briarproject.bramble.api.contact.PendingContactState
import org.briarproject.bramble.api.contact.event.PendingContactAddedEvent
import org.briarproject.bramble.api.db.Transaction
import org.briarproject.bramble.api.db.TransactionManager
import org.briarproject.bramble.api.event.Event
import org.briarproject.bramble.api.event.EventBus
import org.briarproject.bramble.api.lifecycle.LifecycleManager
import org.briarproject.briar.api.conversation.ConversationManager
import org.briarproject.briar.api.conversation.event.ConversationMessageTrackedEvent
import org.briarproject.briar.api.identity.AuthorManager
import org.briarproject.briar.desktop.contact.add.remote.PendingContactItem
import org.briarproject.briar.desktop.conversation.ConversationMessagesReadEvent
import org.briarproject.briar.desktop.threading.BriarExecutors
import org.briarproject.briar.desktop.utils.KLoggerUtils.i
import org.briarproject.briar.desktop.utils.clearAndAddAll
import org.briarproject.briar.desktop.utils.removeFirst
import org.briarproject.briar.desktop.viewmodel.asState
import javax.inject.Inject

class ContactListViewModel
@Inject
constructor(
    contactManager: ContactManager,
    authorManager: AuthorManager,
    conversationManager: ConversationManager,
    connectionRegistry: ConnectionRegistry,
    briarExecutors: BriarExecutors,
    lifecycleManager: LifecycleManager,
    db: TransactionManager,
    eventBus: EventBus,
) : ContactsViewModel(
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

    override fun onInit() {
        super.onInit()
        loadContacts()
    }

    private val _pendingContactList = mutableStateListOf<PendingContactItem>()

    private val _filterBy = mutableStateOf("")
    private val _selectedContactListItem = mutableStateOf<ContactListItem?>(null)
    private val _contactIdToBeRemoved = mutableStateOf<PendingContactId?>(null)

    // todo: check impact on performance due to reconstructing whole list on every change
    val combinedContactList = derivedStateOf {
        (_contactList + _pendingContactList)
            .filter {
                it.displayName.contains(_filterBy.value, ignoreCase = true)
            }.sortedByDescending { it.timestamp }
    }

    val noContactsYet = derivedStateOf {
        _contactList.isEmpty() && _pendingContactList.isEmpty()
    }

    val filterBy = _filterBy.asState()
    val selectedContactListItem = derivedStateOf {
        // reset selected contact to null if not part of list after filtering
        val item = _selectedContactListItem.value
        if (item == null || combinedContactList.value.find { it.uniqueId.contentEquals(item.uniqueId) } != null) {
            item
        } else {
            _selectedContactListItem.value = null
            null
        }
    }
    val removePendingContactDialogVisible = derivedStateOf { _contactIdToBeRemoved.value != null }

    override fun loadContactsWithinTransaction(txn: Transaction) {
        // load real contacts
        super.loadContactsWithinTransaction(txn)

        // load pending contacts
        val pendingContactList = contactManager.getPendingContacts(txn).map { contact ->
            PendingContactItem(contact.first, contact.second)
        }
        txn.attach {
            _pendingContactList.clearAndAddAll(pendingContactList)
        }
    }

    fun selectContact(contactItem: ContactListItem) {
        _selectedContactListItem.value = contactItem
    }

    fun isSelected(contactItem: ContactListItem) =
        _selectedContactListItem.value?.uniqueId.contentEquals(contactItem.uniqueId)

    fun removePendingContact(contactItem: PendingContactItem) {
        if (contactItem.state == PendingContactState.FAILED) {
            // no need to show warning dialog for failed pending contacts
            removePendingContact(contactItem.id)
        } else {
            // show warning dialog
            _contactIdToBeRemoved.value = contactItem.id
        }
    }

    fun confirmRemovingPendingContact() {
        _contactIdToBeRemoved.value?.let { id ->
            removePendingContact(id)
        }
    }

    fun dismissRemovePendingContactDialog() {
        _contactIdToBeRemoved.value = null
    }

    private fun removePendingContact(contactId: PendingContactId) {
        runOnDbThreadWithTransaction(false) { txn ->
            contactManager.removePendingContact(txn, contactId)
            _contactIdToBeRemoved.value = null
            txn.attach {
                removePendingContactItem(contactId)
            }
        }
    }

    fun setFilterBy(filter: String) {
        _filterBy.value = filter
    }

    override fun eventOccurred(e: Event) {
        super.eventOccurred(e)
        when (e) {
            is ConversationMessageTrackedEvent -> {
                LOG.i { "Conversation message tracked, updating item" }
                updateContactItem(e.contactId) { it.updateTimestampAndUnread(e.timestamp, e.read) }
            }

            is ConversationMessagesReadEvent -> {
                LOG.i { "${e.count} conversation messages read, updating item" }
                updateContactItem(e.contactId) { it.updateFromMessagesRead(e.count) }
            }

            is PendingContactAddedEvent -> {
                LOG.i { "Pending contact added, reloading" }
                loadContacts()
            }

            // todo: is PendingContactRemovedEvent
            // todo: is PendingContactStateChangedEvent
        }
    }

    private fun removePendingContactItem(contactId: PendingContactId) =
        _pendingContactList.removeFirst { it.id == contactId }
}
