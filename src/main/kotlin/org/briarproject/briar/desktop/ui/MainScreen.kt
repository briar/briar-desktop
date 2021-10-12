package org.briarproject.briar.desktop.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import org.briarproject.briar.desktop.contact.ContactsViewModel
import org.briarproject.briar.desktop.conversation.PrivateMessageView
import org.briarproject.briar.desktop.conversation.VerticalDivider
import org.briarproject.briar.desktop.settings.PlaceHolderSettingsView
import org.briarproject.briar.desktop.views.BriarSidebar

/*
 * This is the root of the tree, all state is held here and passed down to stateless composables, which render the UI
 * Desktop specific kotlin files are found in briarComposeDesktop (possibly briar-compose-desktop project in the future)
 * Multiplatform, stateless, composable are found in briarCompose (possible briar-compose project in the future)
 */
@Composable
fun MainScreen(
    contactsViewModel: ContactsViewModel,
    isDark: Boolean,
    setDark: (Boolean) -> Unit
) {
    // current selected mode, changed using the sidebar buttons
    val (uiMode, setUiMode) = remember { mutableStateOf(UiMode.CONTACTS) }
    // TODO Figure out how to handle accounts with 0 contacts
    // current selected contact
    val (contact, setContact) = remember { mutableStateOf(contactsViewModel.getFirst()) }
    // Other global state that we need to track should go here also
    Row {
        BriarSidebar(uiMode, setUiMode)
        VerticalDivider()
        when (uiMode) {
            UiMode.CONTACTS -> if (contact != null) PrivateMessageView(
                contact,
                contactsViewModel,
                setContact
            )
            UiMode.SETTINGS -> PlaceHolderSettingsView(isDark, setDark)
            else -> Surface(modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.background)) {
                Text("TBD")
            }
        }
    }
}
