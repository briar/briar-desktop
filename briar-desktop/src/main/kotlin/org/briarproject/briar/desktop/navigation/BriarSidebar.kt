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

package org.briarproject.briar.desktop.navigation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChromeReaderMode
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WifiTethering
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import org.briarproject.bramble.api.identity.LocalAuthor
import org.briarproject.briar.desktop.contact.ProfileCircle
import org.briarproject.briar.desktop.theme.sidebarSurface
import org.briarproject.briar.desktop.ui.UiMode
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n
import org.briarproject.briar.desktop.utils.getConfiguration

val SIDEBAR_WIDTH = 56.dp

@Composable
fun BriarSidebar(
    account: LocalAuthor?,
    uiMode: UiMode,
    setUiMode: (UiMode) -> Unit,
    messageCount: SidebarViewModel.MessageCount,
) {
    @Composable
    fun BriarSidebarButton(
        mode: UiMode,
        icon: ImageVector,
        messageCount: Int = 0,
    ) = BriarSidebarButton(
        uiMode == mode,
        { setUiMode(mode) },
        icon,
        i18n(mode.contentDescriptionKey),
        messageCount
    )

    Surface(
        modifier = Modifier.width(SIDEBAR_WIDTH).fillMaxHeight().selectableGroup(),
        color = MaterialTheme.colors.sidebarSurface
    ) {
        val configuration = getConfiguration()
        Column(verticalArrangement = Arrangement.Top) {
            // profile button
            Box(
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 5.dp, bottom = 4.dp)
            ) {
                account?.let { ProfileCircle(size = 45.dp, it.id.bytes) }
            }

            BriarSidebarButton(UiMode.CONTACTS, Icons.Filled.Contacts, messageCount.privateMessages)
            if (configuration.shouldEnablePrivateGroups()) BriarSidebarButton(UiMode.GROUPS, Icons.Filled.Group)
            if (configuration.shouldEnableForums()) BriarSidebarButton(UiMode.FORUMS, Icons.Filled.Forum, messageCount.forumPosts)
            if (configuration.shouldEnableBlogs()) BriarSidebarButton(UiMode.BLOGS, Icons.Filled.ChromeReaderMode)
        }
        Column(verticalArrangement = Arrangement.Bottom) {
            if (configuration.shouldEnableTransportSettings()) BriarSidebarButton(UiMode.TRANSPORTS, Icons.Filled.WifiTethering)
            BriarSidebarButton(UiMode.SETTINGS, Icons.Filled.Settings)
            BriarSidebarButton(UiMode.ABOUT, Icons.Filled.Info)
        }
    }
}

@Composable
fun BriarSidebarButton(
    selected: Boolean,
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String,
    messageCount: Int,
) = Box {
    val tint = if (selected) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface
    IconButton(
        icon = icon,
        iconSize = 30.dp,
        iconTint = tint,
        contentDescription = contentDescription,
        onClick = onClick,
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 12.dp),
    )
    if (messageCount > 0) {
        val color = MaterialTheme.colors.secondary
        Canvas(modifier = Modifier.align(Alignment.TopEnd).requiredSize(12.dp).offset((-12).dp, 12.dp)) {
            drawCircle(color)
        }
    }
}
