package org.briarproject.briar.desktop.paul.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import org.briarproject.bramble.api.contact.Contact

enum class UiModes {
    CONTACTS,
    GROUPS,
    FORUMS,
    BLOGS,
    TRANSPORTS,
    SETTINGS,
    SIGNOUT
}

/*
 * This is the root of the tree, all state is held here and passed down to stateless composables, which render the UI
 * Desktop specific kotlin files are found in briarComposeDesktop (possibly briar-compose-desktop project in the future)
 * Multiplatform, stateless, composable are found in briarCompose (possible briar-compose project in the future)
 */
@Composable
fun BriarUIStateManager(
    contacts: List<Contact>,
    isDark: Boolean,
    setDark: (Boolean) -> Unit
) {
    // current selected mode, changed using the sidebar buttons
    val (uiMode, setUiMode) = remember { mutableStateOf(UiModes.CONTACTS) }
    // TODO Figure out how to handle accounts with 0 contacts
    // current selected contact
    val (contact, setContact) = remember { mutableStateOf(contact(contacts)) }
    // current selected private group
    val (group, setGroup) = remember { mutableStateOf(contact(contacts)) }
    // current selected forum
    val (forum, setForum) = remember { mutableStateOf(0) }
    // current blog state
    val (blog, setBlog) = remember { mutableStateOf(0) }
    // current transport state
    val (transport, setTransport) = remember { mutableStateOf(0) }
    // current settings state
    val (setting, setSetting) = remember { mutableStateOf(0) }
    // Other global state that we need to track should go here also
    Row {
        BriarSidebar(uiMode, setUiMode)
        VerticalDivider()
        when (uiMode) {
            UiModes.CONTACTS -> if (contact != null) PrivateMessageView(
                contact,
                contacts,
                setContact
            )
            UiModes.SETTINGS -> PlaceHolderSettingsView(isDark, setDark)
            else -> Surface(modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.background)) {
                Text("TBD")
            }
        }
    }
}

@Composable
fun PlaceHolderSettingsView(isDark: Boolean, setDark: (Boolean) -> Unit) {
    Surface(Modifier.fillMaxSize()) {
        Box {
            Button(onClick = { setDark(!isDark) }) {
                Text("Change Theme")
            }
        }
    }
}

fun contact(contacts: List<Contact>): Contact? {
    return if (contacts.isEmpty()) null else contacts[0]
}
