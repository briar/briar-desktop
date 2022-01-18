/*
 * Briar Desktop
 * Copyright (C) 2021-2022 The Briar Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.briarproject.briar.desktop.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import org.briarproject.briar.desktop.conversation.PrivateMessageScreen
import org.briarproject.briar.desktop.navigation.BriarSidebar
import org.briarproject.briar.desktop.navigation.SidebarViewModel
import org.briarproject.briar.desktop.privategroups.PrivateGroupScreen
import org.briarproject.briar.desktop.settings.SettingsScreen
import org.briarproject.briar.desktop.settings.SettingsViewModel
import org.briarproject.briar.desktop.viewmodel.viewModel

/*
 * This is the root of the tree, all state is held here and passed down to stateless composables, which render the UI
 * Desktop specific kotlin files are found in briarComposeDesktop (possibly briar-compose-desktop project in the future)
 * Multiplatform, stateless, composable are found in briarCompose (possible briar-compose project in the future)
 */
@Composable
fun MainScreen(
    settingsViewModel: SettingsViewModel,
    viewModel: SidebarViewModel = viewModel(),
) {
    Row {
        BriarSidebar(
            viewModel.account.value,
            viewModel.uiMode.value,
            viewModel::setUiMode,
        )
        TopAppBarDivider()
        when (viewModel.uiMode.value) {
            UiMode.CONTACTS -> PrivateMessageScreen()
            UiMode.GROUPS -> PrivateGroupScreen()
            UiMode.SETTINGS -> SettingsScreen(settingsViewModel)
            else -> UiPlaceholder()
        }
    }
}
