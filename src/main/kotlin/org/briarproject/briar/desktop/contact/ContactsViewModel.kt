package org.briarproject.briar.desktop.contact

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import org.briarproject.bramble.api.connection.ConnectionRegistry
import org.briarproject.bramble.api.contact.ContactId
import org.briarproject.bramble.api.contact.ContactManager
import org.briarproject.bramble.api.contact.event.ContactAddedEvent
import org.briarproject.bramble.api.contact.event.ContactAliasChangedEvent
import org.briarproject.bramble.api.contact.event.ContactRemovedEvent
import org.briarproject.bramble.api.event.Event
import org.briarproject.bramble.api.event.EventBus
import org.briarproject.bramble.api.event.EventListener
import org.briarproject.bramble.api.plugin.event.ContactConnectedEvent
import org.briarproject.bramble.api.plugin.event.ContactDisconnectedEvent
import org.briarproject.briar.api.conversation.ConversationManager
import org.briarproject.briar.api.conversation.event.ConversationMessageReceivedEvent
import org.briarproject.briar.api.identity.AuthorManager
import org.briarproject.briar.desktop.utils.removeFirst
import org.briarproject.briar.desktop.utils.replaceFirst
import java.util.logging.Logger
import javax.inject.Inject

class ContactsViewModel
@Inject
constructor(
    private val contactManager: ContactManager,
    private val authorManager: AuthorManager,
    private val conversationManager: ConversationManager,
    private val connectionRegistry: ConnectionRegistry,
    private val eventBus: EventBus,
) : EventListener {

    companion object {
        private val LOG = Logger.getLogger(ContactsViewModel::class.java.name)
    }

    init {
        //todo: where/when to remove listener again?
        eventBus.addListener(this)
    }

    private val _contactList = mutableListOf<ContactItem>()
    private val _filteredContactList = mutableStateListOf<ContactItem>()
    private val _filterBy = mutableStateOf("")
    private var _selectedContactIndex = -1;
    private val _selectedContact = mutableStateOf<ContactItem?>(null)

    val contactList: List<ContactItem> = _filteredContactList
    val filterBy: State<String> = _filterBy
    val selectedContact: State<ContactItem?> = _selectedContact

    internal fun loadContacts() {
        _contactList.apply {
            clear()
            addAll(contactManager.contacts.map { contact ->
                ContactItem(
                    contact,
                    authorManager.getAuthorInfo(contact),
                    conversationManager.getGroupCount(contact.id),
                    connectionRegistry.isConnected(contact.id)
                )
            })
        }
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
            addAll(_contactList.filter {
                // todo: also filter on alias?
                it.contact.author.name.lowercase().contains(_filterBy.value)
            })
        }
    }

    fun setFilterBy(filter: String) {
        _filterBy.value = filter
        updateFilteredList()
    }

    override fun eventOccurred(e: Event?) {
        when (e) {
            is ContactAddedEvent -> {
                LOG.info("Contact added, reloading")
                loadContacts()
            }
            is ContactConnectedEvent -> {
                LOG.info("Contact connected, update state")
                updateItem(e.contactId) { it.updateIsConnected(true) }
            }
            is ContactDisconnectedEvent -> {
                LOG.info("Contact disconnected, update state")
                updateItem(e.contactId) { it.updateIsConnected(false) }
            }
            is ContactRemovedEvent -> {
                LOG.info("Contact removed, removing item")
                removeItem(e.contactId)
            }
            is ConversationMessageReceivedEvent<*> -> {
                LOG.info("Conversation message received, updating item")
                updateItem(e.contactId) { it.updateFromMessageHeader(e.messageHeader) }
            }
            //is AvatarUpdatedEvent -> {}
            is ContactAliasChangedEvent -> {
                updateItem(e.contactId) { it.updateAlias(e.alias) }
            }
        }
    }

    private fun updateItem(contactId: ContactId, update: (ContactItem) -> ContactItem) {
        _contactList.replaceFirst({ it.contact.id == contactId }, update)
        updateFilteredList()
    }

    private fun removeItem(contactId: ContactId) {
        _contactList.removeFirst { it.contact.id == contactId }
        updateFilteredList()
    }
}
