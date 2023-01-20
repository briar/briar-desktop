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

import androidx.compose.runtime.mutableStateListOf
import mu.KotlinLogging
import org.briarproject.bramble.api.connection.ConnectionRegistry
import org.briarproject.bramble.api.contact.Contact
import org.briarproject.bramble.api.contact.ContactId
import org.briarproject.bramble.api.contact.ContactManager
import org.briarproject.bramble.api.contact.event.ContactAddedEvent
import org.briarproject.bramble.api.contact.event.ContactAliasChangedEvent
import org.briarproject.bramble.api.contact.event.ContactRemovedEvent
import org.briarproject.bramble.api.db.Transaction
import org.briarproject.bramble.api.db.TransactionManager
import org.briarproject.bramble.api.event.Event
import org.briarproject.bramble.api.event.EventBus
import org.briarproject.bramble.api.lifecycle.LifecycleManager
import org.briarproject.bramble.api.plugin.event.ContactConnectedEvent
import org.briarproject.bramble.api.plugin.event.ContactDisconnectedEvent
import org.briarproject.briar.api.avatar.event.AvatarUpdatedEvent
import org.briarproject.briar.api.conversation.ConversationManager
import org.briarproject.briar.api.identity.AuthorInfo
import org.briarproject.briar.api.identity.AuthorManager
import org.briarproject.briar.desktop.threading.BriarExecutors
import org.briarproject.briar.desktop.threading.UiExecutor
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
    briarExecutors: BriarExecutors,
    lifecycleManager: LifecycleManager,
    db: TransactionManager,
    eventBus: EventBus,
) : EventListenerDbViewModel(briarExecutors, lifecycleManager, db, eventBus) {

    companion object {
        private val LOG = KotlinLogging.logger {}
    }

    protected val _contactList = mutableStateListOf<ContactItem>()

    @UiExecutor
    fun loadContacts() = runOnDbThreadWithTransaction(true, ::loadContactsWithinTransaction)

    open fun loadContactsWithinTransaction(txn: Transaction) {
        val contactList = contactManager.getContacts(txn).map { contact ->
            loadContactItemWithinTransaction(txn, contact)
        }

        txn.attach {
            _contactList.clearAndAddAll(contactList)
        }
    }

    open fun loadContactItemWithinTransaction(txn: Transaction, contact: Contact) =
        loadContactItem(txn, contact, authorManager, connectionRegistry, conversationManager)

    override fun eventOccurred(e: Event?) {
        when (e) {
            is ContactAddedEvent -> {
                // todo: instead, add single new item!
                LOG.i { "Contact added, reloading" }
                loadContacts()
            }

            is ContactConnectedEvent -> {
                LOG.i { "Contact connected, update state" }
                updateContactItem(e.contactId) { it.updateIsConnected(true) }
            }

            is ContactDisconnectedEvent -> {
                LOG.i { "Contact disconnected, update state" }
                updateContactItem(e.contactId) { it.updateIsConnected(false) }
            }

            is ContactRemovedEvent -> {
                LOG.i { "Contact removed, removing item" }
                removeContactItem(e.contactId)
            }

            is ContactAliasChangedEvent -> {
                updateContactItem(e.contactId) { it.updateAlias(e.alias) }
            }

            is AvatarUpdatedEvent -> {
                LOG.i { "received avatar update: ${e.attachmentHeader}" }
                updateContactItem(e.contactId) {
                    val authorInfo =
                        AuthorInfo(it.authorInfo.status, it.authorInfo.alias, e.attachmentHeader)
                    it.updateAuthorInfo(authorInfo)
                }
            }
        }
    }

    protected fun updateContactItem(contactId: ContactId, update: (ContactItem) -> ContactItem) =
        _contactList.replaceFirst({ it.id == contactId }, update)

    protected fun removeContactItem(contactId: ContactId) =
        _contactList.removeFirst { it.id == contactId }
}
