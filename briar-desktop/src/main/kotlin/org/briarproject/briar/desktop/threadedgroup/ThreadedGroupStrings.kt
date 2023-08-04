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

package org.briarproject.briar.desktop.threadedgroup

abstract class ThreadedGroupStrings(
    val listTitle: String,
    val listDescription: String,
    val addGroupTitle: String,
    val addGroupHint: String,
    val addGroupButton: String,
    val noGroupsYet: String,
    val noGroupSelectedTitle: String,
    val noGroupSelectedText: String,
    val messageCount: (Int) -> String,
    val unreadCount: (Int) -> String,
    val lastMessage: (String) -> String,
    val groupDissolved: String = "",
    val groupNameMaxLength: Int,
    val sharedWith: (total: Int, online: Int) -> String,
    val unreadJumpToPrevious: String,
    val unreadJumpToNext: String,
    val deleteDialogTitle: (Boolean) -> String,
    val deleteDialogMessage: (Boolean) -> String,
    val deleteDialogButton: (Boolean) -> String,
    val messageMaxLength: Int,
    val messageReplyIntro: String,
    val messageReplyClose: String,
    val messageReplyHint: String,
    val messageHint: String,
    override val sharingActionTitle: String,
    override val sharingActionClose: String,
    override val sharingActionNoContacts: String,
) : SharingStrings

interface SharingStrings {
    val sharingActionTitle: String
    val sharingActionClose: String
    val sharingActionNoContacts: String
}
