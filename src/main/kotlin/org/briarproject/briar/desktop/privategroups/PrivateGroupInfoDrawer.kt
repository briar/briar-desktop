package org.briarproject.briar.desktop.privategroups

import androidx.compose.runtime.Composable
import org.briarproject.briar.api.privategroup.PrivateGroup
import org.briarproject.briar.desktop.contact.ContactInfoDrawerState

// Right drawer state
enum class PrivateGroupInfoDrawerState {
    MakeIntro,
    ConnectBT,
    ConnectRD
}

@Composable
fun ContactInfoDrawer(
    privateGroup: PrivateGroup,
    setInfoDrawer: (Boolean) -> Unit,
    drawerState: ContactInfoDrawerState
) {
    /* TODO
    when (drawerState) {
        MakeIntro -> ContactDrawerMakeIntro(privateGroup, setInfoDrawer)
    }
     */
}
