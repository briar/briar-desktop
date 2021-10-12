package org.briarproject.briar.desktop.dialogs

import androidx.compose.runtime.mutableStateListOf
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

    internal val contacts = mutableStateListOf<Contact>()

    internal fun loadContacts() {
        val contacts = contactManager.contacts
        for (contact in contacts) {
            LOG.info("loaded contact: ${contact.author.name} (${contact.alias})")
            this.contacts.add(contact)
        }
    }
}
