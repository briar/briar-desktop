package org.briarproject.briar.desktop.contact

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import org.briarproject.bramble.api.contact.Contact
import org.briarproject.bramble.api.contact.ContactManager
import java.util.logging.Logger
import javax.inject.Inject

class ContactsViewModel
@Inject
constructor(
    private val contactManager: ContactManager,
) {

    companion object {
        private val LOG = Logger.getLogger(ContactsViewModel::class.java.name)
    }

    private val _contactList = mutableListOf<Contact>()
    private val _filteredContactList = mutableStateListOf<Contact>()
    private val _filterBy = mutableStateOf("")
    private var _selectedContactIndex = -1;
    private val _selectedContact = mutableStateOf<Contact?>(null)

    val contactList: List<Contact> = _filteredContactList
    val filterBy: State<String> = _filterBy
    val selectedContact: State<Contact?> = _selectedContact

    internal fun loadContacts() {
        _contactList.apply {
            clear()
            addAll(contactManager.contacts)
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
                it.author.name.lowercase().contains(_filterBy.value)
            })
        }
    }

    fun setFilterBy(filter: String) {
        _filterBy.value = filter
        updateFilteredList()
    }
}
