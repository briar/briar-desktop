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

package org.briarproject.briar.desktop.forum.conversation

import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import org.briarproject.briar.desktop.forum.ForumStrings
import org.briarproject.briar.desktop.forum.sharing.ForumSharingStatusDrawerContent
import org.briarproject.briar.desktop.forum.sharing.ForumSharingViewModel
import org.briarproject.briar.desktop.threadedgroup.sharing.ThreadedGroupSharingActionDrawerContent
import org.briarproject.briar.desktop.ui.getInfoDrawerHandler
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n

@Composable
fun ForumDropdownMenu(
    forumSharingViewModel: ForumSharingViewModel,
    expanded: Boolean,
    onClose: () -> Unit,
    onMarkReadClick: () -> Unit,
    onLeaveForumClick: () -> Unit,
) = DropdownMenu(
    expanded = expanded,
    onDismissRequest = onClose,
) {
    val infoDrawerHandler = getInfoDrawerHandler()
    DropdownMenuItem(
        onClick = {
            onClose()
            infoDrawerHandler.open {
                ThreadedGroupSharingActionDrawerContent(
                    close = infoDrawerHandler::close,
                    viewModel = forumSharingViewModel,
                    strings = ForumStrings,
                )
            }
        }
    ) {
        Text(
            i18n("forum.sharing.action.title"),
            style = MaterialTheme.typography.body2,
        )
    }
    DropdownMenuItem(
        onClick = {
            onClose()
            infoDrawerHandler.open {
                ForumSharingStatusDrawerContent(
                    close = infoDrawerHandler::close,
                    viewModel = forumSharingViewModel,
                )
            }
        }
    ) {
        Text(
            i18n("forum.sharing.status.title"),
            style = MaterialTheme.typography.body2,
        )
    }
    DropdownMenuItem(
        onClick = {
            onClose()
            onMarkReadClick()
        }
    ) {
        Text(
            i18n("group.mark.read"),
            style = MaterialTheme.typography.body2,
        )
    }
    DropdownMenuItem(
        onClick = {
            onClose()
            onLeaveForumClick()
        }
    ) {
        Text(
            i18n("forum.leave.title"),
            style = MaterialTheme.typography.body2,
        )
    }
}
