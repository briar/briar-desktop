package org.briarproject.briar.desktop.contact

import androidx.compose.runtime.Composable
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
    contactItem: ContactItem,
    closeInfoDrawer: (reload: Boolean) -> Unit,
    drawerState: ContactInfoDrawerState
) {
    when (drawerState) {
        MakeIntro -> ContactDrawerMakeIntro(contactItem, closeInfoDrawer)
    }
}
