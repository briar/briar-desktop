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
import androidx.compose.material.DrawerValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import org.briarproject.briar.desktop.conversation.PrivateMessageScreen
import org.briarproject.briar.desktop.forums.ForumScreen
import org.briarproject.briar.desktop.navigation.BriarSidebar
import org.briarproject.briar.desktop.navigation.SidebarViewModel
import org.briarproject.briar.desktop.privategroups.PrivateGroupScreen
import org.briarproject.briar.desktop.settings.SettingsScreen
import org.briarproject.briar.desktop.viewmodel.viewModel

/*
 * This is the root of the tree, all state is held here and passed down to stateless composables, which render the UI
 * Desktop specific kotlin files are found in briarComposeDesktop (possibly briar-compose-desktop project in the future)
 * Multiplatform, stateless, composable are found in briarCompose (possible briar-compose project in the future)
 */
@Composable
fun MainScreen(viewModel: SidebarViewModel = viewModel()) {
    val drawerHandler = remember { InfoDrawerHandler() }
    InfoDrawer(
        drawerState = drawerHandler.state,
        drawerContent = {
            drawerHandler.content()
        }
    ) {
        CompositionLocalProvider(LocalInfoDrawerHandler provides drawerHandler) {
            Row {
                BriarSidebar(
                    viewModel.account.value,
                    viewModel.uiMode.value,
                    viewModel::setUiMode,
                    viewModel.messageCount.value,
                )
                VerticalDivider()
                when (viewModel.uiMode.value) {
                    UiMode.CONTACTS -> PrivateMessageScreen()
                    UiMode.GROUPS -> PrivateGroupScreen()
                    UiMode.FORUMS -> ForumScreen()
                    UiMode.SETTINGS -> SettingsScreen()
                    UiMode.ABOUT -> AboutScreen()
                    else -> UiPlaceholder()
                }
            }
        }
    }
}

val LocalInfoDrawerHandler = staticCompositionLocalOf<InfoDrawerHandler?> { null }

@Composable
fun getInfoDrawerHandler() = checkNotNull(LocalInfoDrawerHandler.current) {
    "No InfoDrawerHandler was provided via LocalInfoDrawerHandler" // NON-NLS
}

/**
 * Handler to interact with the current [InfoDrawer].
 * Should be provided via [LocalInfoDrawerHandler] and retrieved using [getInfoDrawerHandler].
 */
class InfoDrawerHandler(
    val state: InfoDrawerState = InfoDrawerState(DrawerValue.Closed),
    initialContent: @Composable () -> Unit = {},
) {

    var content by mutableStateOf(initialContent)
        private set

    /**
     * Open the associated [InfoDrawer] with the given [content].
     *
     * @param content Composable content to be shown in the [InfoDrawer].
     *  May be null, in which case the last content will be shown again.
     */
    fun open(content: (@Composable () -> Unit)? = null) {
        if (content != null) this.content = content
        state.open()
    }

    /**
     * Close the associated [InfoDrawer].
     */
    fun close() = state.close()
}
