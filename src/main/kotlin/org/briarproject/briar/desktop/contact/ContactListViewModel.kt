package org.briarproject.briar.desktop.contact

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import org.briarproject.bramble.api.connection.ConnectionRegistry
import org.briarproject.bramble.api.contact.ContactId
import org.briarproject.bramble.api.contact.ContactManager
import org.briarproject.bramble.api.contact.event.ContactAliasChangedEvent
import org.briarproject.bramble.api.event.Event
import org.briarproject.bramble.api.event.EventBus
import org.briarproject.briar.api.conversation.ConversationManager
import org.briarproject.briar.api.conversation.event.ConversationMessageReceivedEvent
import java.util.logging.Logger
import javax.inject.Inject

class ContactListViewModel
@Inject
constructor(
    contactManager: ContactManager,
    conversationManager: ConversationManager,
    connectionRegistry: ConnectionRegistry,
    eventBus: EventBus,
) : ContactsViewModel(contactManager, conversationManager, connectionRegistry) {

    companion object {
        private val LOG = Logger.getLogger(ContactListViewModel::class.java.name)
    }

    init {
        // todo: where/when to remove listener again?
        eventBus.addListener(this)
    }

    private val _filteredContactList = mutableStateListOf<ContactItem>()
    private val _filterBy = mutableStateOf("")
    private var _selectedContactIndex = -1
    private val _selectedContact = mutableStateOf<ContactItem?>(null)

    override val contactList: List<ContactItem> = _filteredContactList
    val filterBy: State<String> = _filterBy
    val selectedContact: State<ContactItem?> = _selectedContact

    override fun loadContacts() {
        super.loadContacts()
        updateFilteredList()
    }

    fun selectContact(index: Int) {
        _selectedContactIndex = index
        _selectedContact.value = _filteredContactList[index]
    }

    fun isSelected(index: Int) = _selectedContactIndex == index

    private fun updateFilteredList() {
        _filteredContactList.apply {
            clear()
            addAll(
                _contactList.filter {
                    // todo: also filter on alias?
                    it.contact.author.name.lowercase().contains(_filterBy.value)
                }
            )
        }
    }

    fun setFilterBy(filter: String) {
        _filterBy.value = filter
        updateFilteredList()
    }

    override fun eventOccurred(e: Event?) {
        super.eventOccurred(e)
        when (e) {
            is ConversationMessageReceivedEvent<*> -> {
                LOG.info("Conversation message received, updating item")
                updateItem(e.contactId) { it.updateFromMessageHeader(e.messageHeader) }
            }
            // is AvatarUpdatedEvent -> {}
            is ContactAliasChangedEvent -> {
                updateItem(e.contactId) { it.updateAlias(e.alias) }
            }
        }
    }

    override fun updateItem(contactId: ContactId, update: (ContactItem) -> ContactItem) {
        super.updateItem(contactId, update)
        updateFilteredList()
    }

    override fun removeItem(contactId: ContactId) {
        super.removeItem(contactId)
        updateFilteredList()
    }
}
