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

package org.briarproject.briar.desktop.threadedgroup.conversation

import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize.Max
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.AlertDialog
import androidx.compose.material.ButtonType.DESTRUCTIVE
import androidx.compose.material.ButtonType.NEUTRAL
import androidx.compose.material.DialogButton
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
import androidx.compose.ui.unit.dp
import org.briarproject.briar.desktop.contact.ContactDropDown.State.CLOSED
import org.briarproject.briar.desktop.contact.ContactDropDown.State.MAIN
import org.briarproject.briar.desktop.privategroup.sharing.PrivateGroupSharingViewModel
import org.briarproject.briar.desktop.threadedgroup.ThreadedGroupCircle
import org.briarproject.briar.desktop.threadedgroup.ThreadedGroupItem
import org.briarproject.briar.desktop.threadedgroup.ThreadedGroupStrings
import org.briarproject.briar.desktop.threadedgroup.sharing.ThreadedGroupSharingViewModel
import org.briarproject.briar.desktop.ui.Constants.HEADER_SIZE
import org.briarproject.briar.desktop.ui.HorizontalDivider
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n

@Composable
fun ThreadedGroupConversationScreen(
    strings: ThreadedGroupStrings,
    viewModel: ThreadedConversationViewModel,
    dropdownMenu: ThreadedGroupDropdownMenu,
    extraContent: (@Composable () -> Unit)? = null,
) {
    Scaffold(
        topBar = {
            viewModel.groupItem.value?.let { groupItem ->
                ThreadedGroupConversationHeader(
                    strings = strings,
                    threadedGroupItem = groupItem,
                    sharingViewModel = viewModel.sharingViewModel,
                    onGroupDelete = viewModel::deleteGroup,
                    onMarkRead = viewModel::markAllThreadItemsRead,
                    dropdownMenu = dropdownMenu,
                )
            }
        },
        content = { padding ->
            extraContent?.let { it() }
            ThreadedGroupConversationContent(
                strings = strings,
                state = viewModel.state.value,
                selectedThreadItem = viewModel.selectedThreadItem.value,
                onThreadItemSelected = viewModel::selectThreadItem,
                onThreadItemsVisible = viewModel::markThreadItemsRead,
                modifier = Modifier.padding(padding).alpha(if (viewModel.groupEnabled) 1f else 0.5f)
            )
        },
        bottomBar = {
            if (viewModel.groupEnabled) {
                // only show message compose field is group is enabled (aka not dissolved for private groups)
                val onCloseReply = { viewModel.selectThreadItem(null) }
                ThreadedGroupConversationInput(strings, viewModel.selectedThreadItem.value, onCloseReply) { text ->
                    viewModel.createThreadItem(text)
                }
            }
        }
    )
}

@Composable
private fun ThreadedGroupConversationHeader(
    strings: ThreadedGroupStrings,
    threadedGroupItem: ThreadedGroupItem,
    sharingViewModel: ThreadedGroupSharingViewModel,
    onMarkRead: () -> Unit,
    onGroupDelete: () -> Unit,
    dropdownMenu: ThreadedGroupDropdownMenu,
) {
    val deleteGroupDialogVisible = remember { mutableStateOf(false) }
    val menuState = remember { mutableStateOf(CLOSED) }
    val close = { menuState.value = CLOSED }
    Box(modifier = Modifier.fillMaxWidth().height(HEADER_SIZE + 1.dp)) {
        Row(
            horizontalArrangement = SpaceBetween,
            modifier = Modifier.fillMaxWidth().align(Center),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(start = 8.dp)
                    .weight(1f, fill = false),
                horizontalArrangement = spacedBy(12.dp),
                verticalAlignment = CenterVertically
            ) {
                ThreadedGroupCircle(threadedGroupItem)
                Column {
                    Text(
                        modifier = Modifier,
                        text = threadedGroupItem.name,
                        maxLines = 1,
                        overflow = Ellipsis,
                        style = MaterialTheme.typography.h2,
                    )
                    val sharingInfo = sharingViewModel.sharingInfo.value
                    Text(
                        text = strings.sharedWith(sharingInfo.total, sharingInfo.online)
                    )
                }
            }
            IconButton(
                icon = Icons.Filled.MoreVert,
                contentDescription = i18n("access.menu"),
                onClick = { menuState.value = MAIN },
                modifier = Modifier.align(CenterVertically).padding(end = 16.dp),
            ) {
                dropdownMenu(
                    sharingViewModel,
                    menuState.value == MAIN,
                    close,
                    onMarkRead,
                ) { deleteGroupDialogVisible.value = true }
            }
        }
        HorizontalDivider(modifier = Modifier.align(BottomCenter))
    }
    if (deleteGroupDialogVisible.value) {
        DeleteThreadedGroupDialog(
            strings = strings,
            close = { deleteGroupDialogVisible.value = false },
            isCreator = if (sharingViewModel is PrivateGroupSharingViewModel)
                sharingViewModel.isCreator.value else false,
            onDelete = onGroupDelete,
        )
    }
}

@Composable
@OptIn(ExperimentalMaterialApi::class)
private fun DeleteThreadedGroupDialog(
    strings: ThreadedGroupStrings,
    isCreator: Boolean,
    close: () -> Unit,
    onDelete: () -> Unit = {},
) {
    AlertDialog(
        onDismissRequest = close,
        title = {
            Text(
                text = strings.deleteDialogTitle(isCreator),
                modifier = Modifier.width(Max),
                style = MaterialTheme.typography.h6,
            )
        },
        text = {
            Text(strings.deleteDialogMessage(isCreator))
        },
        dismissButton = {
            DialogButton(
                onClick = { close() },
                text = i18n("cancel"),
                type = NEUTRAL,
            )
        },
        confirmButton = {
            DialogButton(
                onClick = {
                    close()
                    onDelete()
                },
                text = strings.deleteDialogButton(isCreator),
                type = DESTRUCTIVE,
            )
        },
    )
}
