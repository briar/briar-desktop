package org.briarproject.briar.desktop.contact

import androidx.compose.runtime.Composable
import org.briarproject.bramble.api.contact.Contact
import org.briarproject.briar.desktop.contact.ContactInfoDrawerState.MakeIntro

// Right drawer state
enum class ContactInfoDrawerState {
    MakeIntro,
    ConnectBT,
    ConnectRD
}

@Composable
fun ContactInfoDrawer(
    contact: Contact,
    contacts: List<Contact>,
    setInfoDrawer: (Boolean) -> Unit,
    drawerState: ContactInfoDrawerState
) {
    when (drawerState) {
        MakeIntro -> ContactDrawerMakeIntro(contact, contacts, setInfoDrawer)
    }
}
