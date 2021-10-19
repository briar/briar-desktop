package org.briarproject.briar.desktop.contact

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
import java.util.logging.Logger

abstract class ContactsViewModel(
    protected val contactManager: ContactManager,
    private val conversationManager: ConversationManager,
    private val connectionRegistry: ConnectionRegistry
) : EventListener {

    companion object {
        private val LOG = Logger.getLogger(ContactsViewModel::class.java.name)
    }

    protected val _contactList = mutableListOf<ContactItem>()

    abstract val contactList: List<ContactItem>

    protected open fun filterContact(contact: Contact) = true

    open fun loadContacts() {
        _contactList.apply {
            clear()
            addAll(contactManager.contacts.filter(::filterContact).map { contact ->
                ContactItem(
                    contact,
                    connectionRegistry.isConnected(contact.id),
                    conversationManager.getGroupCount(contact.id),
                )
            })
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
        _contactList.replaceFirst({ it.contact.id == contactId }, update)
    }

    protected open fun removeItem(contactId: ContactId) {
        _contactList.removeFirst { it.contact.id == contactId }
    }
}
