package org.briarproject.briar.desktop.contact

import androidx.compose.runtime.Composable
import org.briarproject.bramble.api.contact.Contact
import org.briarproject.briar.desktop.contact.ContactInfoDrawerState.MakeIntro
import org.briarproject.briar.desktop.introduction.ContactDrawerMakeIntro

// Right drawer state
enum class ContactInfoDrawerState {
    MakeIntro,
    ConnectBT,
    ConnectRD
}

@Composable
fun ContactInfoDrawer(
    contact: Contact,
    setInfoDrawer: (Boolean) -> Unit,
    drawerState: ContactInfoDrawerState
) {
    when (drawerState) {
        MakeIntro -> ContactDrawerMakeIntro(contact, setInfoDrawer)
    }
}
