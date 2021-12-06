package org.briarproject.briar.desktop.contact

import androidx.compose.runtime.mutableStateOf
import mu.KotlinLogging
import org.briarproject.bramble.api.connection.ConnectionRegistry
import org.briarproject.bramble.api.contact.ContactId
import org.briarproject.bramble.api.contact.ContactManager
import org.briarproject.bramble.api.contact.event.ContactAliasChangedEvent
import org.briarproject.bramble.api.db.TransactionManager
import org.briarproject.bramble.api.event.Event
import org.briarproject.bramble.api.event.EventBus
import org.briarproject.bramble.api.lifecycle.LifecycleManager
import org.briarproject.briar.api.conversation.ConversationManager
import org.briarproject.briar.api.conversation.event.ConversationMessageTrackedEvent
import org.briarproject.briar.desktop.conversation.ConversationMessagesReadEvent
import org.briarproject.briar.desktop.threading.BriarExecutors
import org.briarproject.briar.desktop.viewmodel.asState
import javax.inject.Inject

class ContactListViewModel
@Inject
constructor(
    contactManager: ContactManager,
    conversationManager: ConversationManager,
    connectionRegistry: ConnectionRegistry,
    briarExecutors: BriarExecutors,
    lifecycleManager: LifecycleManager,
    db: TransactionManager,
    eventBus: EventBus,
) : ContactsViewModel(
    contactManager, conversationManager, connectionRegistry, briarExecutors, lifecycleManager, db, eventBus
) {

    companion object {
        private val LOG = KotlinLogging.logger {}
    }

    override fun onInit() {
        super.onInit()
        loadContacts()
    }

    private val _filterBy = mutableStateOf("")
    private val _selectedContactId = mutableStateOf<ContactId?>(null)

    val filterBy = _filterBy.asState()
    val selectedContactId = _selectedContactId.asState()

    fun selectContact(contactId: ContactId) {
        _selectedContactId.value = contactId
    }

    fun isSelected(contactId: ContactId) = _selectedContactId.value == contactId

    override fun filterContactItem(contactItem: ContactItem) =
        contactItem.displayName.contains(_filterBy.value, ignoreCase = true)

    fun setFilterBy(filter: String) {
        _filterBy.value = filter
        updateFilteredList()
    }

    override fun updateFilteredList() {
        super.updateFilteredList()

        // reset selected contact to null if not available after filtering
        val id = _selectedContactId.value
        if (id != null && !contactList.map { it.contactId }.contains(id)) {
            _selectedContactId.value = null
        }
    }

    override fun eventOccurred(e: Event?) {
        super.eventOccurred(e)
        when (e) {
            is ConversationMessageTrackedEvent -> {
                LOG.info { "Conversation message tracked, updating item" }
                updateItem(e.contactId) { it.updateTimestampAndUnread(e.timestamp, e.read) }
            }
            // is AvatarUpdatedEvent -> {}
            is ContactAliasChangedEvent -> {
                updateItem(e.contactId) { it.updateAlias(e.alias) }
            }
            is ConversationMessagesReadEvent -> {
                LOG.info("${e.count} conversation messages read, updating item")
                updateItem(e.contactId) { it.updateFromMessagesRead(e.count) }
            }
        }
    }
}
