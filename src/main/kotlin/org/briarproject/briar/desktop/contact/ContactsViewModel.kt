package org.briarproject.briar.desktop.contact

import androidx.compose.runtime.mutableStateListOf
import mu.KotlinLogging
import org.briarproject.bramble.api.connection.ConnectionRegistry
import org.briarproject.bramble.api.contact.Contact
import org.briarproject.bramble.api.contact.ContactId
import org.briarproject.bramble.api.contact.ContactManager
import org.briarproject.bramble.api.contact.event.ContactAddedEvent
import org.briarproject.bramble.api.contact.event.ContactRemovedEvent
import org.briarproject.bramble.api.event.Event
import org.briarproject.bramble.api.event.EventListener
import org.briarproject.bramble.api.plugin.event.ContactConnectedEvent
import org.briarproject.bramble.api.plugin.event.ContactDisconnectedEvent
import org.briarproject.briar.api.conversation.ConversationManager
import org.briarproject.briar.desktop.utils.removeFirst
import org.briarproject.briar.desktop.utils.replaceFirst

abstract class ContactsViewModel(
    protected val contactManager: ContactManager,
    private val conversationManager: ConversationManager,
    private val connectionRegistry: ConnectionRegistry
) : EventListener {

    companion object {
        private val LOG = KotlinLogging.logger {}
    }

    private val _fullContactList = mutableListOf<ContactItem>()
    private val _filteredContactList = mutableStateListOf<ContactItem>()

    val contactList: List<ContactItem> = _filteredContactList

    protected open fun filterContact(contact: Contact) = true

    open fun loadContacts() {
        _fullContactList.apply {
            clear()
            addAll(
                contactManager.contacts.map { contact ->
                    ContactItem(
                        contact,
                        connectionRegistry.isConnected(contact.id),
                        conversationManager.getGroupCount(contact.id),
                    )
                }
            )
        }
        updateFilteredList()
    }

    // todo: when migrated to StateFlow, this could be done implicitly instead
    protected open fun updateFilteredList() {
        _filteredContactList.apply {
            clear()
            addAll(_fullContactList.filter { filterContact(it.contact) }.sortedByDescending { it.timestamp })
        }
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
        }
    }

    protected open fun updateItem(contactId: ContactId, update: (ContactItem) -> ContactItem) {
        _fullContactList.replaceFirst({ it.contact.id == contactId }, update)
        updateFilteredList()
    }

    protected open fun removeItem(contactId: ContactId) {
        _fullContactList.removeFirst { it.contact.id == contactId }
        updateFilteredList()
    }
}
