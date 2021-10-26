package org.briarproject.briar.desktop.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import org.briarproject.briar.desktop.contact.ContactListViewModel
import org.briarproject.briar.desktop.contact.add.remote.AddContactViewModel
import org.briarproject.briar.desktop.conversation.ConversationViewModel
import org.briarproject.briar.desktop.conversation.PrivateMessageView
import org.briarproject.briar.desktop.introduction.IntroductionViewModel
import org.briarproject.briar.desktop.navigation.BriarSidebar
import org.briarproject.briar.desktop.navigation.SidebarViewModel
import org.briarproject.briar.desktop.settings.PlaceHolderSettingsView

/*
 * This is the root of the tree, all state is held here and passed down to stateless composables, which render the UI
 * Desktop specific kotlin files are found in briarComposeDesktop (possibly briar-compose-desktop project in the future)
 * Multiplatform, stateless, composable are found in briarCompose (possible briar-compose project in the future)
 */
@Composable
fun MainScreen(
    contactListViewModel: ContactListViewModel,
    conversationViewModel: ConversationViewModel,
    addContactViewModel: AddContactViewModel,
    introductionViewModel: IntroductionViewModel,
    sidebarViewModel: SidebarViewModel,
    isDark: Boolean,
    setDark: (Boolean) -> Unit
) {
    Row {
        BriarSidebar(sidebarViewModel)
        VerticalDivider()
        when (sidebarViewModel.uiMode.value) {
            UiMode.CONTACTS -> PrivateMessageView(
                contactListViewModel,
                conversationViewModel,
                addContactViewModel,
                introductionViewModel
            )
            UiMode.SETTINGS -> PlaceHolderSettingsView(isDark, setDark)
            else -> UiPlaceholder()
        }
    }
}
