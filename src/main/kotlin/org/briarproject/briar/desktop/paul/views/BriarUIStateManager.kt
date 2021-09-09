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
import org.briarproject.briar.desktop.paul.data.ContactList
import org.briarproject.briar.desktop.paul.theme.briarBlack

/*
 * This is the root of the tree, all state is held here and passed down to stateless composables, which render the UI
 * Desktop specific kotlin files are found in briarComposeDesktop (possibly briar-compose-desktop project in the future)
 * Multiplatform, stateless, composable are found in briarCompose (possible briar-compose project in the future)
 */
@Composable
fun briarUIStateManager() {
    //current selected mode, changed using the sidebar buttons
    val (UIMode, onModeChange) = remember { mutableStateOf("Contacts") }
    //current selected contact
    val (UIContact, onContactSelect) = remember { mutableStateOf(ContactList.contacts[0]) }
    //current selected private message
    val (UIPrivateMsg, onPMSelect) = remember { mutableStateOf(0) }
    //current selected forum
    val (UIForum, onForumSelect) = remember { mutableStateOf(0) }
    //current blog state
    val (UIBlog, onBlogSelect) = remember { mutableStateOf(0) }
    //current transport state
    val (UITransports, onTransportSelect) = remember { mutableStateOf(0) }
    //current settings state
    val (UISettings, onSettingSelect) = remember { mutableStateOf(0) }
    //current profile
    var Profile: String;
    //Other global state that we need to track should go here also
    Row() {
        briarSidebar(UIMode, onModeChange)
        when (UIMode) {
            "Contacts" -> privateMessageView(UIContact, onContactSelect)
            else -> Box(modifier = Modifier.fillMaxSize().background(briarBlack)) {
                Text("TBD", modifier = Modifier.align(Alignment.Center), color = Color.White)
            }
        }
    }

}
