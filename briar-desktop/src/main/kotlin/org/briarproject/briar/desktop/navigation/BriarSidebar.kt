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

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement.Absolute.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Badge
import androidx.compose.material.BadgedBox
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import org.briarproject.bramble.api.identity.Author
import org.briarproject.bramble.api.plugin.Plugin
import org.briarproject.briar.desktop.contact.ProfileCircle
import org.briarproject.briar.desktop.navigation.SidebarButtonState.None
import org.briarproject.briar.desktop.navigation.SidebarButtonState.UnreadMessages
import org.briarproject.briar.desktop.navigation.SidebarButtonState.Warning
import org.briarproject.briar.desktop.theme.Lime500
import org.briarproject.briar.desktop.theme.Orange500
import org.briarproject.briar.desktop.theme.outline
import org.briarproject.briar.desktop.theme.sidebarSurface
import org.briarproject.briar.desktop.ui.Tooltip
import org.briarproject.briar.desktop.ui.UiMode
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n
import org.briarproject.briar.desktop.utils.getConfiguration

val SIDEBAR_WIDTH = 56.dp

@Composable
fun BriarSidebar(
    account: Author?,
    uiMode: UiMode,
    setUiMode: (UiMode) -> Unit,
    messageCount: SidebarViewModel.MessageCount,
    torPluginState: Plugin.State,
    hasMailboxProblem: Boolean,
) {
    @Composable
    fun BriarSidebarButton(
        mode: UiMode,
        messageCount: Int = 0,
    ) = BriarSidebarButton(
        selected = uiMode == mode,
        onClick = { setUiMode(mode) },
        icon = mode.icon,
        contentDescription = i18n(mode.contentDescriptionKey),
        sideBarButtonState = if (messageCount == 0) None else UnreadMessages(messageCount),
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
        SideBarAvatar(account, torPluginState)

        Spacer(Modifier.height(4.dp))

        BriarSidebarButton(UiMode.CONTACTS, messageCount.privateMessages)
        if (configuration.shouldEnablePrivateGroups())
            BriarSidebarButton(UiMode.GROUPS, messageCount.privateGroupMessages)
        if (configuration.shouldEnableForums()) BriarSidebarButton(UiMode.FORUMS, messageCount.forumPosts)
        if (configuration.shouldEnableBlogs()) BriarSidebarButton(UiMode.BLOGS, messageCount.blogPosts)

        Spacer(Modifier.weight(1f))

        if (configuration.shouldEnableTransportSettings()) BriarSidebarButton(UiMode.TRANSPORTS)
        BriarSidebarButton(
            selected = uiMode == UiMode.MAILBOX,
            onClick = { setUiMode(UiMode.MAILBOX) },
            icon = UiMode.MAILBOX.icon,
            contentDescription = i18n(UiMode.MAILBOX.contentDescriptionKey),
            sideBarButtonState = if (hasMailboxProblem) Warning else None,
        )
        BriarSidebarButton(UiMode.SETTINGS)
        BriarSidebarButton(UiMode.ABOUT)
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun SideBarAvatar(account: Author?, torPluginState: Plugin.State) = Box {
    ProfileCircle(size = 45.dp, account?.id?.bytes ?: ByteArray(0))
    Tooltip(
        text = torPluginState.getString(),
        modifier = Modifier.align(BottomEnd),
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .border(1.dp, MaterialTheme.colors.outline, CircleShape)
                .background(torPluginState.getIconColor(), CircleShape)
        )
    }
}

@Composable
private fun Plugin.State.getIconColor(): Color {
    return if (this == Plugin.State.ACTIVE) Lime500
    else if (this == Plugin.State.ENABLING) Orange500
    else MaterialTheme.colors.sidebarSurface
}

private fun Plugin.State.getString(): String {
    return if (this == Plugin.State.ACTIVE) i18n("transports.tor.active")
    else if (this == Plugin.State.ENABLING) i18n("transports.tor.enabling")
    else i18n("transports.tor.inactive")
}

sealed class SidebarButtonState {
    object None : SidebarButtonState()
    class UnreadMessages(val messageCount: Int) : SidebarButtonState()
    object Warning : SidebarButtonState()
}

@Composable
fun BriarSidebarButton(
    selected: Boolean,
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String,
    sideBarButtonState: SidebarButtonState,
) = BadgedBox(
    badge = {
        if (sideBarButtonState is UnreadMessages && sideBarButtonState.messageCount > 0) {
            Badge(
                modifier = Modifier.offset((-12).dp, 12.dp),
                backgroundColor = MaterialTheme.colors.secondary,
            )
        } else if (sideBarButtonState is Warning) {
            Icon(
                Icons.Default.Error,
                i18n("mailbox.status.problem"),
                modifier = Modifier.offset((-12).dp, 12.dp).size(16.dp),
                tint = MaterialTheme.colors.error
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
