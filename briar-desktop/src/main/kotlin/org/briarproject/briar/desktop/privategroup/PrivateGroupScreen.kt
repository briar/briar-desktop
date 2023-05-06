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

package org.briarproject.briar.desktop.privategroup

import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.width
import androidx.compose.material.AlertDialog
import androidx.compose.material.ButtonType
import androidx.compose.material.DialogButton
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import org.briarproject.briar.desktop.privategroup.conversation.PrivateGroupDropdownMenu
import org.briarproject.briar.desktop.privategroup.sharing.PrivateGroupSharingViewModel
import org.briarproject.briar.desktop.threadedgroup.ThreadedGroupScreen
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n
import org.briarproject.briar.desktop.viewmodel.viewModel

@Composable
fun PrivateGroupScreen(
    viewModel: PrivateGroupListViewModel = viewModel(),
) {
    ThreadedGroupScreen(
        strings = PrivateGroupStrings,
        viewModel = viewModel,
        dropdownMenu = { sharingViewModel, expanded, onClose, onLeaveOrDissolvePrivateGroupClick ->
            val privateGroupSharingViewModel = sharingViewModel as PrivateGroupSharingViewModel
            PrivateGroupDropdownMenu(
                privateGroupSharingViewModel = privateGroupSharingViewModel,
                expanded = expanded,
                onClose = onClose,
                onLeaveOrDissolvePrivateGroupClick = onLeaveOrDissolvePrivateGroupClick
            )
        },
        extraContent = {
            if (viewModel.threadViewModel.isDissolved.value) {
                PrivateGroupDissolvedDialog()
            }
        }
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun PrivateGroupDissolvedDialog() {
    val showDissolvedDialog = remember { mutableStateOf(true) }
    if (showDissolvedDialog.value) {
        val close = { showDissolvedDialog.value = false }
        AlertDialog(
            onDismissRequest = close,
            title = {
                Text(
                    text = i18n("group.dissolved.dialog.title"),
                    modifier = Modifier.width(IntrinsicSize.Max),
                    style = MaterialTheme.typography.h6,
                )
            },
            text = {
                Text(i18n("group.dissolved.dialog.message"))
            },
            confirmButton = {
                DialogButton(
                    onClick = { close() },
                    text = i18n("ok"),
                    type = ButtonType.NEUTRAL,
                )
            },
        )
    }
}
