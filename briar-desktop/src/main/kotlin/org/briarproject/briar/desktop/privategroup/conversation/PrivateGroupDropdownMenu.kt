/*
 * Briar Desktop
 * Copyright (C) 2023 The Briar Project
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

package org.briarproject.briar.desktop.privategroup.conversation

import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import org.briarproject.briar.desktop.privategroup.sharing.PrivateGroupMemberDrawerContent
import org.briarproject.briar.desktop.privategroup.sharing.PrivateGroupSharingViewModel
import org.briarproject.briar.desktop.ui.getInfoDrawerHandler
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n

@Composable
fun PrivateGroupDropdownMenu(
    privateGroupSharingViewModel: PrivateGroupSharingViewModel,
    expanded: Boolean,
    onClose: () -> Unit,
    onLeaveOrDissolvePrivateGroupClick: () -> Unit,
) = DropdownMenu(
    expanded = expanded,
    onDismissRequest = onClose,
) {
    val infoDrawerHandler = getInfoDrawerHandler()
    DropdownMenuItem(
        onClick = {
            onClose()
            infoDrawerHandler.open {
                PrivateGroupMemberDrawerContent(
                    close = infoDrawerHandler::close,
                    viewModel = privateGroupSharingViewModel,
                )
            }
        }
    ) {
        Text(
            i18n("group.member.title"),
            style = MaterialTheme.typography.body2,
        )
    }
    if (privateGroupSharingViewModel.isCreator.value) {
        // todo: invite member #496
    }
    DropdownMenuItem(
        onClick = {
            onClose()
            onLeaveOrDissolvePrivateGroupClick()
        }
    ) {
        Text(
            if (privateGroupSharingViewModel.isCreator.value) i18n("group.dissolve.title")
            else i18n("group.leave.title"),
            style = MaterialTheme.typography.body2,
        )
    }
}
