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

package org.briarproject.briar.desktop.forum

import androidx.compose.runtime.Composable
import org.briarproject.briar.desktop.forum.conversation.ForumDropdownMenu
import org.briarproject.briar.desktop.forum.sharing.ForumSharingViewModel
import org.briarproject.briar.desktop.threadedgroup.ThreadedGroupScreen
import org.briarproject.briar.desktop.viewmodel.viewModel

@Composable
fun ForumScreen(
    viewModel: ForumListViewModel = viewModel(),
) = ThreadedGroupScreen(
    strings = ForumStrings,
    viewModel = viewModel,
    dropdownMenu = { sharingViewModel, expanded, onClose, onMarkReadClick, onLeaveForumClick ->
        val forumSharingViewModel = sharingViewModel as ForumSharingViewModel
        ForumDropdownMenu(forumSharingViewModel, expanded, onClose, onMarkReadClick, onLeaveForumClick)
    }
)
