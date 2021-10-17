package org.briarproject.briar.desktop.contact

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import org.briarproject.bramble.api.FormatException
import org.briarproject.bramble.api.connection.ConnectionRegistry
import org.briarproject.bramble.api.contact.ContactManager
import org.briarproject.bramble.api.db.ContactExistsException
import org.briarproject.bramble.api.db.PendingContactExistsException
import org.briarproject.bramble.api.identity.AuthorConstants
import org.briarproject.bramble.util.StringUtils
import org.briarproject.briar.api.conversation.ConversationManager
import org.briarproject.briar.api.identity.AuthorManager
import java.security.GeneralSecurityException
import java.util.logging.Logger
import javax.inject.Inject

class ContactsViewModel
@Inject
constructor(
    private val contactManager: ContactManager,
    private val authorManager: AuthorManager,
    private val conversationManager: ConversationManager,
    private val connectionRegistry: ConnectionRegistry,
) {

    companion object {
        private val LOG = Logger.getLogger(ContactsViewModel::class.java.name)
    }

    private val _contactList = mutableListOf<ContactItem>()
    private val _filteredContactList = mutableStateListOf<ContactItem>()
    private val _filterBy = mutableStateOf("")
    private var _selectedContactIndex = -1;
    private val _selectedContact = mutableStateOf<ContactItem?>(null)

    private val _addContactDialogVisible = mutableStateOf(false)
    private val _addContactAlias = mutableStateOf("")
    private val _addContactLink = mutableStateOf("")

    val contactList: List<ContactItem> = _filteredContactList
    val filterBy: State<String> = _filterBy
    val selectedContact: State<ContactItem?> = _selectedContact

    val addContactDialogVisible: State<Boolean> = _addContactDialogVisible
    val addContactAlias: State<String> = _addContactAlias
    val addContactLink: State<String> = _addContactLink
    var addContactOwnLink = ""

    internal fun loadContacts() {
        _contactList.apply {
            clear()
            addAll(contactManager.contacts.map { contact ->
                ContactItem(
                    contact,
                    authorManager.getAuthorInfo(contact),
                    connectionRegistry.isConnected(contact.id),
                    conversationManager.getGroupCount(contact.id)
                )
            })
        }
        updateFilteredList()
        addContactOwnLink = contactManager.handshakeLink
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

    fun openAddContactDialog() {
        _addContactDialogVisible.value = true
    }

    fun closeAddContactDialog() {
        _addContactDialogVisible.value = false
    }

    fun setAddContactAlias(alias: String) {
        _addContactAlias.value = alias
    }

    fun setAddContactLink(link: String) {
        _addContactLink.value = link
    }

    fun onSubmitAddContactDialog() {
        val link = _addContactLink.value
        val alias = _addContactAlias.value
        addPendingContact(link, alias)
        closeAddContactDialog()
    }

    private fun addPendingContact(link: String, alias: String) {
        if (addContactOwnLink.equals(link)) {
            println("Please enter contact's link, not your own")
            return
        }
        if (aliasIsInvalid(alias)) {
            println("Alias is invalid")
            return
        }

        try {
            contactManager.addPendingContact(link, alias)
        } catch (e: FormatException) {
            println("Link is invalid")
            println(e.stackTrace)
        } catch (e: GeneralSecurityException) {
            println("Public key is invalid")
            println(e.stackTrace)
        }
        /*
        TODO: Warn user that the following two errors might be an attack

         Use `e.pendingContact.id.bytes` and `e.pendingContact.alias` to implement the following logic:
         https://code.briarproject.org/briar/briar-gtk/-/merge_requests/97

        */
        catch (e: ContactExistsException) {
            println("Contact already exists")
            println(e.stackTrace)
        } catch (e: PendingContactExistsException) {
            println("Pending Contact already exists")
            println(e.stackTrace)
        }
    }

    private fun aliasIsInvalid(alias: String): Boolean {
        val aliasUtf8 = StringUtils.toUtf8(alias)
        return aliasUtf8.isEmpty() || aliasUtf8.size > AuthorConstants.MAX_AUTHOR_NAME_LENGTH
    }
}
