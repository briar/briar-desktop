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

package org.briarproject.briar.desktop.privategroup

import org.briarproject.briar.api.privategroup.PrivateGroupConstants.MAX_GROUP_NAME_LENGTH
import org.briarproject.briar.api.privategroup.PrivateGroupConstants.MAX_GROUP_POST_TEXT_LENGTH
import org.briarproject.briar.desktop.group.GroupStrings
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18nF
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18nP

// todo: replace with strings for private groups
object PrivateGroupStrings : GroupStrings(
    listTitle = i18n("forum.search.title"),
    listDescription = i18n("access.forums.list"),
    addGroupTitle = i18n("forum.add.title"),
    addGroupHint = i18n("forum.add.hint"),
    addGroupButton = i18n("forum.add.button"),
    noGroupsYet = i18n("forum.empty_state.text"),
    noGroupSelectedTitle = i18n("forum.none_selected.title"),
    noGroupSelectedText = i18n("forum.none_selected.hint"),
    messageCount = { count ->
        if (count > 0) i18nP("group.card.posts", count)
        else i18n("group.card.no_posts")
    },
    unreadCount = { count ->
        i18nP("access.forums.unread_count", count)
    },
    lastMessage = { timestamp ->
        i18nF("access.forums.last_post_timestamp", timestamp)
    },
    groupNameMaxLength = MAX_GROUP_NAME_LENGTH,
    sharedWith = { total, online ->
        i18nF("forum.sharing.status.with", total, online)
    },
    unreadJumpToPrevious = i18n("access.forums.jump_to_prev_unread"),
    unreadJumpToNext = i18n("access.forums.jump_to_next_unread"),
    deleteDialogTitle = i18n("forum.delete.dialog.title"),
    deleteDialogMessage = i18n("forum.delete.dialog.message"),
    deleteDialogButton = i18n("forum.delete.dialog.button"),
    messageMaxLength = MAX_GROUP_POST_TEXT_LENGTH,
    messageReplyIntro = i18n("forum.message.reply.intro"),
    messageReplyClose = i18n("access.forums.reply.close"),
    messageReplyHint = i18n("forum.message.reply.hint"),
    messageHint = i18n("forum.message.hint"),
)
