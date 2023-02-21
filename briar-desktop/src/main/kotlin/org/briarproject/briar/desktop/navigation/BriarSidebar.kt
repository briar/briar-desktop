/*
 * Briar Desktop
 * Copyright (C) 2021-2023 The Briar Project
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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.Badge
import androidx.compose.material.BadgedBox
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
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
        messageCount: Int = 0,
    ) = BriarSidebarButton(
        uiMode == mode,
        { setUiMode(mode) },
        mode.icon,
        i18n(mode.contentDescriptionKey),
        messageCount
    )

    val configuration = getConfiguration()
    Column(
        modifier = Modifier.background(MaterialTheme.colors.sidebarSurface)
            .width(SIDEBAR_WIDTH).fillMaxHeight()
            .padding(vertical = 4.dp)
            .selectableGroup(),
        verticalArrangement = spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // profile button
        account?.let { ProfileCircle(size = 45.dp, it.id.bytes) }

        Spacer(Modifier.height(4.dp))

        BriarSidebarButton(UiMode.CONTACTS, messageCount.privateMessages)
        if (configuration.shouldEnablePrivateGroups()) BriarSidebarButton(UiMode.GROUPS)
        if (configuration.shouldEnableForums()) BriarSidebarButton(UiMode.FORUMS, messageCount.forumPosts)
        if (configuration.shouldEnableBlogs()) BriarSidebarButton(UiMode.BLOGS)

        Spacer(Modifier.weight(1f))

        if (configuration.shouldEnableTransportSettings()) BriarSidebarButton(UiMode.TRANSPORTS)
        if (configuration.shouldEnableMailbox()) BriarSidebarButton(UiMode.MAILBOX)
        BriarSidebarButton(UiMode.SETTINGS)
        BriarSidebarButton(UiMode.ABOUT)
    }
}

@Composable
fun BriarSidebarButton(
    selected: Boolean,
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String,
    messageCount: Int,
) = BadgedBox(
    badge = {
        if (messageCount > 0) {
            Badge(
                modifier = Modifier.offset((-12).dp, 12.dp),
                backgroundColor = MaterialTheme.colors.secondary,
            )
        }
    },
) {
    val tint = if (selected) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface
    IconButton(
        icon = icon,
        iconSize = 30.dp,
        iconTint = tint,
        contentDescription = contentDescription,
        onClick = onClick,
    )
}
