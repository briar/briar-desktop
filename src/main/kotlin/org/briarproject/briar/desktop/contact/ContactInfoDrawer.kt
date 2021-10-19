package org.briarproject.briar.desktop.contact

import androidx.compose.runtime.Composable
import org.briarproject.briar.desktop.contact.ContactInfoDrawerState.MakeIntro
import org.briarproject.briar.desktop.introduction.ContactDrawerMakeIntro
import org.briarproject.briar.desktop.introduction.IntroductionViewModel

// Right drawer state
enum class ContactInfoDrawerState {
    MakeIntro,
    ConnectBT,
    ConnectRD
}

@Composable
fun ContactInfoDrawer(
    introductionViewModel: IntroductionViewModel,
    setInfoDrawer: (Boolean) -> Unit,
    drawerState: ContactInfoDrawerState
) {
    when (drawerState) {
        MakeIntro -> ContactDrawerMakeIntro(introductionViewModel, setInfoDrawer)
    }
}
