package org.briarproject.briar.desktop.paul.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.briarproject.bramble.api.contact.Contact
import org.briarproject.briar.desktop.paul.theme.briarBlack

/*
 * This is the root of the tree, all state is held here and passed down to stateless composables, which render the UI
 * Desktop specific kotlin files are found in briarComposeDesktop (possibly briar-compose-desktop project in the future)
 * Multiplatform, stateless, composable are found in briarCompose (possible briar-compose project in the future)
 */
@Composable
fun BriarUIStateManager(
    contacts: List<Contact>
) {
    // current selected mode, changed using the sidebar buttons
    val (uiMode, setUiMode) = remember { mutableStateOf("Contacts") }
    // TODO Figure out how to handle accounts with 0 contacts
    // current selected contact
    val (contact, setContact) = remember { mutableStateOf(contacts[0]) }
    // current selected forum
    val (forum, setForum) = remember { mutableStateOf(0) }
    // current blog state
    val (blog, setBlog) = remember { mutableStateOf(0) }
    // current transport state
    val (transport, setTransport) = remember { mutableStateOf(0) }
    // current settings state
    val (setting, setSetting) = remember { mutableStateOf(0) }
    // Other global state that we need to track should go here also
    Row() {
        BriarSidebar(uiMode, setUiMode)
        when (uiMode) {
            "Contacts" -> PrivateMessageView(
                contact,
                contacts,
                setContact
            )
            else -> Box(modifier = Modifier.fillMaxSize().background(briarBlack)) {
                Text("TBD", modifier = Modifier.align(Alignment.Center), color = Color.White)
            }
        }
    }
}
