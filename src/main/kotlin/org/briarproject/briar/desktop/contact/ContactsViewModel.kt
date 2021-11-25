package org.briarproject.briar.desktop.contact

import androidx.compose.runtime.mutableStateListOf
import mu.KotlinLogging
import org.briarproject.bramble.api.connection.ConnectionRegistry
import org.briarproject.bramble.api.contact.ContactId
import org.briarproject.bramble.api.contact.ContactManager
import org.briarproject.bramble.api.contact.event.ContactAddedEvent
import org.briarproject.bramble.api.contact.event.ContactRemovedEvent
import org.briarproject.bramble.api.db.TransactionManager
import org.briarproject.bramble.api.event.Event
import org.briarproject.bramble.api.event.EventBus
import org.briarproject.bramble.api.lifecycle.LifecycleManager
import org.briarproject.bramble.api.plugin.event.ContactConnectedEvent
import org.briarproject.bramble.api.plugin.event.ContactDisconnectedEvent
import org.briarproject.briar.api.conversation.ConversationManager
import org.briarproject.briar.desktop.threading.BriarExecutors
import org.briarproject.briar.desktop.utils.clearAndAddAll
import org.briarproject.briar.desktop.utils.removeFirst
import org.briarproject.briar.desktop.utils.replaceFirst
import org.briarproject.briar.desktop.viewmodel.EventListenerDbViewModel

abstract class ContactsViewModel(
    protected val contactManager: ContactManager,
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

    private val _fullContactList = mutableStateListOf<ContactItem>()
    private val _filteredContactList = mutableStateListOf<ContactItem>()

    val contactList: List<ContactItem> = _filteredContactList

    protected open fun filterContactItem(contactItem: ContactItem) = true

    open fun loadContacts() {
        val contactList = mutableListOf<ContactItem>()
        // TODO: move this inside transaction once briar has transactional method for this
        contactList.addAll(
            contactManager.pendingContacts.map { contact ->
                PendingContactItem(contact.first)
            }
        )
        runOnDbThreadWithTransaction(true) { txn ->
            contactList.addAll(
                contactManager.getContacts(txn).map { contact ->
                    RealContactItem(
                        contact,
                        connectionRegistry.isConnected(contact.id),
                        conversationManager.getGroupCount(txn, contact.id),
                    )
                }
            )
            txn.attach {
                _fullContactList.clearAndAddAll(contactList)
                updateFilteredList()
            }
        }
    }

    // todo: when migrated to StateFlow, this could be done implicitly instead
    protected open fun updateFilteredList() {
        _filteredContactList.clearAndAddAll(
            _fullContactList.filter(::filterContactItem).sortedByDescending { it.timestamp }
        )
    }

    override fun eventOccurred(e: Event?) {
        when (e) {
            is ContactAddedEvent -> {
                LOG.info("Contact added, reloading")
                loadContacts()
            }
            is ContactConnectedEvent -> {
                LOG.info("Contact connected, update state")
                updateItem(e.contactId) {
                    if (it is RealContactItem)
                        it.updateIsConnected(true)
                    else it
                }
            }
            is ContactDisconnectedEvent -> {
                LOG.info("Contact disconnected, update state")
                updateItem(e.contactId) {
                    if (it is RealContactItem)
                        it.updateIsConnected(false)
                    else it
                }
            }
            is ContactRemovedEvent -> {
                LOG.info("Contact removed, removing item")
                removeItem(e.contactId)
            }
        }
    }

    protected open fun updateItem(contactId: ContactId, update: (ContactItem) -> ContactItem) {
        _fullContactList.replaceFirst(
            {
                if (it is RealContactItem)
                    it.contactId == contactId
                else false
            },
            update
        )
        updateFilteredList()
    }

    protected open fun removeItem(contactId: ContactId) {
        _fullContactList.removeFirst {
            if (it is RealContactItem)
                it.contactId == contactId
            else false
        }
        updateFilteredList()
    }
}
