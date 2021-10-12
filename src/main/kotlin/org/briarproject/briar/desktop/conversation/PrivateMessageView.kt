package org.briarproject.briar.desktop.conversation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import org.briarproject.bramble.api.contact.Contact
import org.briarproject.briar.desktop.contact.AddContactDialog
import org.briarproject.briar.desktop.contact.ContactInfoDrawerState.MakeIntro
import org.briarproject.briar.desktop.contact.ContactList
import org.briarproject.briar.desktop.contact.ContactsViewModel
import org.briarproject.briar.desktop.ui.VerticalDivider

@Composable
fun PrivateMessageView(
    contact: Contact,
    contacts: ContactsViewModel,
    onContactSelect: (Contact) -> Unit
) {
    val (isDialogVisible, setDialogVisibility) = remember { mutableStateOf(false) }
    val (dropdownExpanded, setExpanded) = remember { mutableStateOf(false) }
    val (infoDrawer, setInfoDrawer) = remember { mutableStateOf(false) }
    val (contactDrawerState, setDrawerState) = remember { mutableStateOf(MakeIntro) }
    AddContactDialog(isDialogVisible, setDialogVisibility)
    Row(modifier = Modifier.fillMaxWidth()) {
        ContactList(contact, contacts.contacts, onContactSelect, setDialogVisibility)
        VerticalDivider()
        Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
            Conversation(
                contact,
                contacts.contacts,
                dropdownExpanded,
                setExpanded,
                infoDrawer,
                setInfoDrawer,
                contactDrawerState
            )
        }
    }
}
