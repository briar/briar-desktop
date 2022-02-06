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
import mu.KotlinLogging
import org.briarproject.bramble.api.connection.ConnectionRegistry
import org.briarproject.bramble.api.contact.ContactId
import org.briarproject.bramble.api.contact.ContactManager
import org.briarproject.bramble.api.contact.event.ContactAddedEvent
import org.briarproject.bramble.api.contact.event.ContactRemovedEvent
import org.briarproject.bramble.api.contact.event.PendingContactAddedEvent
import org.briarproject.bramble.api.db.TransactionManager
import org.briarproject.bramble.api.event.Event
import org.briarproject.bramble.api.event.EventBus
import org.briarproject.bramble.api.lifecycle.LifecycleManager
import org.briarproject.bramble.api.plugin.event.ContactConnectedEvent
import org.briarproject.bramble.api.plugin.event.ContactDisconnectedEvent
import org.briarproject.briar.api.attachment.AttachmentReader
import org.briarproject.briar.api.conversation.ConversationManager
import org.briarproject.briar.api.identity.AuthorManager
import org.briarproject.briar.desktop.threading.BriarExecutors
import org.briarproject.briar.desktop.utils.ImageUtils.loadAvatar
import org.briarproject.briar.desktop.utils.KLoggerUtils.i
import org.briarproject.briar.desktop.utils.clearAndAddAll
import org.briarproject.briar.desktop.utils.removeFirst
import org.briarproject.briar.desktop.utils.replaceFirst
import org.briarproject.briar.desktop.viewmodel.EventListenerDbViewModel

abstract class ContactsViewModel(
    protected val contactManager: ContactManager,
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

    private val _fullContactList = mutableStateListOf<BaseContactItem>()

    val noContactsYet = derivedStateOf {
        _fullContactList.isEmpty()
    }

    val contactList = derivedStateOf {
        _fullContactList.filter(::filterContactItem).sortedByDescending { it.timestamp }
    }

    protected open fun filterContactItem(contactItem: BaseContactItem) = true

    open fun loadContacts() {
        val contactList = mutableListOf<BaseContactItem>()
        runOnDbThreadWithTransaction(true) { txn ->
            contactList.addAll(
                contactManager.getPendingContacts(txn).map { contact ->
                    PendingContactItem(contact.first)
                }
            )
            contactList.addAll(
                contactManager.getContacts(txn).map { contact ->
                    ContactItem(
                        contact,
                        connectionRegistry.isConnected(contact.id),
                        conversationManager.getGroupCount(txn, contact.id),
                        loadAvatar(authorManager, attachmentReader, txn, contact),
                    )
                }
            )
            txn.attach {
                _fullContactList.clearAndAddAll(contactList)
            }
        }
    }

    override fun eventOccurred(e: Event?) {
        when (e) {
            is ContactAddedEvent -> {
                LOG.i { "Contact added, reloading" }
                loadContacts()
            }
            is PendingContactAddedEvent -> {
                LOG.i { "Pending contact added, reloading" }
                loadContacts()
            }
            is ContactConnectedEvent -> {
                LOG.i { "Contact connected, update state" }
                updateItem(e.contactId) {
                    it.updateIsConnected(true)
                }
            }
            is ContactDisconnectedEvent -> {
                LOG.i { "Contact disconnected, update state" }
                updateItem(e.contactId) { it.updateIsConnected(false) }
            }
            is ContactRemovedEvent -> {
                LOG.i { "Contact removed, removing item" }
                removeItem(e.contactId)
            }
        }
    }

    protected open fun updateItem(contactId: ContactId, update: (ContactItem) -> ContactItem) {
        _fullContactList.replaceFirst(
            { it.idWrapper.contactId == contactId },
            update
        )
    }

    protected open fun removeItem(contactId: ContactId) {
        _fullContactList.removeFirst<BaseContactItem, ContactItem> {
            it.idWrapper.contactId == contactId
        }
    }
}
