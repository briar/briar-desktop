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
                LOG.info("Contact added, reloading")
                loadContacts()
            }
            is PendingContactAddedEvent -> {
                LOG.info("Pending contact added, reloading")
                loadContacts()
            }
            is ContactConnectedEvent -> {
                LOG.info("Contact connected, update state")
                updateItem(e.contactId) {
                    it.updateIsConnected(true)
                }
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
