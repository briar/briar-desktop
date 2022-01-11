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
import androidx.compose.runtime.mutableStateOf
import mu.KotlinLogging
import org.briarproject.bramble.api.connection.ConnectionRegistry
import org.briarproject.bramble.api.contact.ContactManager
import org.briarproject.bramble.api.contact.event.ContactAliasChangedEvent
import org.briarproject.bramble.api.db.TransactionManager
import org.briarproject.bramble.api.event.Event
import org.briarproject.bramble.api.event.EventBus
import org.briarproject.bramble.api.lifecycle.LifecycleManager
import org.briarproject.briar.api.attachment.AttachmentReader
import org.briarproject.briar.api.avatar.event.AvatarUpdatedEvent
import org.briarproject.briar.api.conversation.ConversationManager
import org.briarproject.briar.api.conversation.event.ConversationMessageTrackedEvent
import org.briarproject.briar.api.identity.AuthorManager
import org.briarproject.briar.desktop.conversation.ConversationMessagesReadEvent
import org.briarproject.briar.desktop.threading.BriarExecutors
import org.briarproject.briar.desktop.utils.ImageUtils
import org.briarproject.briar.desktop.viewmodel.asState
import javax.inject.Inject

class ContactListViewModel
@Inject
constructor(
    contactManager: ContactManager,
    authorManager: AuthorManager,
    conversationManager: ConversationManager,
    connectionRegistry: ConnectionRegistry,
    private val attachmentReader: AttachmentReader,
    briarExecutors: BriarExecutors,
    lifecycleManager: LifecycleManager,
    db: TransactionManager,
    eventBus: EventBus,
) : ContactsViewModel(
    contactManager,
    authorManager,
    conversationManager,
    connectionRegistry,
    attachmentReader,
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

    private val _filterBy = mutableStateOf("")
    private val _selectedContactId = mutableStateOf<ContactIdWrapper?>(null)

    val filterBy = _filterBy.asState()
    val selectedContactId = derivedStateOf {
        // reset selected contact to null if not part of list after filtering
        val id = _selectedContactId.value
        if (id == null || contactList.value.map { it.idWrapper }.contains(id)) {
            id
        } else {
            _selectedContactId.value = null
            null
        }
    }

    fun selectContact(contactItem: BaseContactItem) {
        _selectedContactId.value = contactItem.idWrapper
    }

    fun isSelected(contactItem: BaseContactItem) = _selectedContactId.value == contactItem.idWrapper

    override fun filterContactItem(contactItem: BaseContactItem) =
        contactItem.displayName.contains(_filterBy.value, ignoreCase = true)

    fun setFilterBy(filter: String) {
        _filterBy.value = filter
    }

    override fun eventOccurred(e: Event?) {
        super.eventOccurred(e)
        when (e) {
            is ConversationMessageTrackedEvent -> {
                LOG.info { "Conversation message tracked, updating item" }
                updateItem(e.contactId) { it.updateTimestampAndUnread(e.timestamp, e.read) }
            }
            is ContactAliasChangedEvent -> {
                updateItem(e.contactId) { it.updateAlias(e.alias) }
            }
            is ConversationMessagesReadEvent -> {
                LOG.info("${e.count} conversation messages read, updating item")
                updateItem(e.contactId) { it.updateFromMessagesRead(e.count) }
            }
            is AvatarUpdatedEvent -> {
                LOG.info("received avatar update: ${e.attachmentHeader}")
                if (e.attachmentHeader == null) {
                    updateItem(e.contactId) { it.updateAvatar(null) }
                } else {
                    runOnDbThreadWithTransaction(true) { txn ->
                        val image = ImageUtils.loadImage(txn, attachmentReader, e.attachmentHeader)
                        txn.attach {
                            updateItem(e.contactId) { it.updateAvatar(image) }
                        }
                    }
                }
            }
        }
    }
}
