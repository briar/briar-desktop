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

import androidx.compose.runtime.Composable
import org.briarproject.briar.desktop.privategroup.conversation.PrivateGroupDropdownMenu
import org.briarproject.briar.desktop.privategroup.sharing.PrivateGroupSharingViewModel
import org.briarproject.briar.desktop.threadedgroup.ThreadedGroupScreen
import org.briarproject.briar.desktop.viewmodel.viewModel

@Composable
fun PrivateGroupScreen(
    viewModel: PrivateGroupListViewModel = viewModel(),
) = ThreadedGroupScreen(
    strings = PrivateGroupStrings,
    viewModel = viewModel,
    dropdownMenu = { sharingViewModel, expanded, onClose, onLeaveForumClick ->
        val privateGroupSharingViewModel = sharingViewModel as PrivateGroupSharingViewModel
        PrivateGroupDropdownMenu(privateGroupSharingViewModel, expanded, onClose, onLeaveForumClick)
    }
)
