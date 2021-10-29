package org.briarproject.briar.desktop.contact

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import org.briarproject.bramble.api.connection.ConnectionRegistry
import org.briarproject.bramble.api.contact.Contact
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

    private val _filterBy = mutableStateOf("")
    private val _selectedContact = mutableStateOf<Contact?>(null)

    val filterBy: State<String> = _filterBy
    val selectedContact: State<Contact?> = _selectedContact

    fun selectContact(contact: Contact) {
        _selectedContact.value = contact
    }

    fun isSelected(contact: Contact) = _selectedContact.value?.id == contact.id

    override fun filterContact(contact: Contact) =
        // todo: also filter on alias
        contact.author.name.contains(_filterBy.value, ignoreCase = true)

    fun setFilterBy(filter: String) {
        _filterBy.value = filter
        updateFilteredList()
    }

    override fun updateFilteredList() {
        super.updateFilteredList()
        _selectedContact.value?.let { if (!filterContact(it)) _selectedContact.value = null }
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
}
