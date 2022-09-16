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

package org.briarproject.briar.desktop.forums

import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
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
import org.briarproject.briar.desktop.ui.Constants.HEADER_SIZE
import org.briarproject.briar.desktop.ui.HorizontalDivider
import org.briarproject.briar.desktop.ui.UiPlaceholder
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n
import org.briarproject.briar.desktop.viewmodel.viewModel

@Composable
fun GroupConversationScreen(
    groupItem: GroupItem,
    viewModel: ForumsViewModel = viewModel(),
) {
    Scaffold(
        topBar = {
            GroupConversationHeader(groupItem) { viewModel.deleteGroup(groupItem) }
        },
        content = { padding ->
            // Loader()
            UiPlaceholder()
        },
    )
}

@Composable
private fun GroupConversationHeader(
    groupItem: GroupItem,
    onGroupDelete: () -> Unit,
) {
    val deleteGroupDialogState = remember { mutableStateOf(false) }
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
            ) {
                GroupCircle(
                    item = groupItem,
                    showMessageCount = false,
                    modifier = Modifier.align(CenterVertically),
                )
                Text(
                    modifier = Modifier
                        .align(CenterVertically)
                        .padding(start = 12.dp)
                        .weight(1f, fill = false),
                    text = groupItem.name,
                    maxLines = 2,
                    overflow = Ellipsis,
                    style = MaterialTheme.typography.h2,
                )
            }
            IconButton(
                icon = Icons.Filled.MoreVert,
                contentDescription = i18n("access.menu"),
                onClick = { menuState.value = MAIN },
                modifier = Modifier.align(CenterVertically).padding(end = 16.dp),
            ) {
                DropdownMenu(
                    expanded = menuState.value == MAIN,
                    onDismissRequest = close,
                ) {
                    DropdownMenuItem(
                        onClick = {
                            close()
                            deleteGroupDialogState.value = true
                        }
                    ) {
                        Text(
                            i18n("forum.leave.title"),
                            style = MaterialTheme.typography.body2,
                        )
                    }
                }
            }
        }
        HorizontalDivider(modifier = Modifier.align(BottomCenter))
    }
    if (deleteGroupDialogState.value) {
        DeleteForumDialog(
            close = { deleteGroupDialogState.value = false },
            onDelete = onGroupDelete,
        )
    }
}

@Composable
@OptIn(ExperimentalMaterialApi::class)
fun DeleteForumDialog(
    close: () -> Unit,
    onDelete: () -> Unit = {},
) {
    AlertDialog(
        onDismissRequest = close,
        title = {
            Text(
                text = i18n("forum.delete.dialog.title"),
                modifier = Modifier.width(Max),
                style = MaterialTheme.typography.h6,
            )
        },
        text = {
            Text(i18n("forum.delete.dialog.message"))
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
                text = i18n("forum.delete.dialog.button"),
                type = DESTRUCTIVE,
            )
        },
    )
}
