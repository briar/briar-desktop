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

package org.briarproject.briar.desktop.group.conversation

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
import androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
import androidx.compose.ui.unit.dp
import org.briarproject.briar.desktop.contact.ContactDropDown.State.CLOSED
import org.briarproject.briar.desktop.contact.ContactDropDown.State.MAIN
import org.briarproject.briar.desktop.forums.sharing.ForumSharingViewModel
import org.briarproject.briar.desktop.group.GroupCircle
import org.briarproject.briar.desktop.group.GroupItem
import org.briarproject.briar.desktop.group.GroupStrings
import org.briarproject.briar.desktop.ui.Constants.HEADER_SIZE
import org.briarproject.briar.desktop.ui.HorizontalDivider
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n

@Composable
fun GroupConversationScreen(
    strings: GroupStrings,
    viewModel: ThreadedConversationViewModel,
    dropdownMenu: GroupDropdownMenu,
) {
    Scaffold(
        topBar = {
            viewModel.groupItem.value?.let { groupItem ->
                GroupConversationHeader(
                    strings = strings,
                    groupItem = groupItem,
                    forumSharingViewModel = viewModel.forumSharingViewModel,
                    onGroupDelete = viewModel::deleteGroup,
                    dropdownMenu = dropdownMenu,
                )
            }
        },
        content = { padding ->
            ThreadedConversationScreen(
                strings = strings,
                state = viewModel.state.value,
                selectedThreadItem = viewModel.selectedThreadItem.value,
                onThreadItemSelected = viewModel::selectThreadItem,
                onThreadItemsVisible = viewModel::markThreadItemsRead,
                modifier = Modifier.padding(padding)
            )
        },
        bottomBar = {
            val onCloseReply = { viewModel.selectThreadItem(null) }
            ThreadedConversationInput(strings, viewModel.selectedThreadItem.value, onCloseReply) { text ->
                viewModel.createThreadItem(text)
            }
        }
    )
}

@Composable
private fun GroupConversationHeader(
    strings: GroupStrings,
    groupItem: GroupItem,
    forumSharingViewModel: ForumSharingViewModel,
    onGroupDelete: () -> Unit,
    dropdownMenu: GroupDropdownMenu,
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
                GroupCircle(groupItem)
                Column {
                    Text(
                        modifier = Modifier,
                        text = groupItem.name,
                        maxLines = 2,
                        overflow = Ellipsis,
                        style = MaterialTheme.typography.h2,
                    )
                    val sharingInfo = forumSharingViewModel.sharingInfo.value
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
                    forumSharingViewModel,
                    menuState.value == MAIN,
                    close
                ) { deleteGroupDialogVisible.value = true }
            }
        }
        HorizontalDivider(modifier = Modifier.align(BottomCenter))
    }
    if (deleteGroupDialogVisible.value) {
        DeleteGroupDialog(
            strings = strings,
            close = { deleteGroupDialogVisible.value = false },
            onDelete = onGroupDelete,
        )
    }
}

@Composable
@OptIn(ExperimentalMaterialApi::class)
private fun DeleteGroupDialog(
    strings: GroupStrings,
    close: () -> Unit,
    onDelete: () -> Unit = {},
) {
    AlertDialog(
        onDismissRequest = close,
        title = {
            Text(
                text = strings.deleteDialogTitle,
                modifier = Modifier.width(Max),
                style = MaterialTheme.typography.h6,
            )
        },
        text = {
            Text(strings.deleteDialogMessage)
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
                text = strings.deleteDialogButton,
                type = DESTRUCTIVE,
            )
        },
    )
}
