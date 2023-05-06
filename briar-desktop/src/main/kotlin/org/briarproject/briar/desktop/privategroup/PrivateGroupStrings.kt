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
import org.briarproject.briar.desktop.threadedgroup.ThreadedGroupStrings
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18nF
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18nP

object PrivateGroupStrings : ThreadedGroupStrings(
    listTitle = i18n("group.search.title"),
    listDescription = i18n("access.group.list"),
    addGroupTitle = i18n("group.add.title"),
    addGroupHint = i18n("group.add.hint"),
    addGroupButton = i18n("group.add.button"),
    noGroupsYet = i18n("group.empty_state.text"),
    noGroupSelectedTitle = i18n("group.none_selected.title"),
    noGroupSelectedText = i18n("group.none_selected.hint"),
    messageCount = { count ->
        if (count > 0) i18nP("group.card.posts", count)
        else i18n("group.card.no_posts")
    },
    unreadCount = { count ->
        i18nP("access.group.unread_count", count)
    },
    lastMessage = { timestamp ->
        i18nF("access.group.last_post_timestamp", timestamp)
    },
    groupNameMaxLength = MAX_GROUP_NAME_LENGTH,
    sharedWith = { total, online ->
        i18nF("group.sharing.status.with", total, online)
    },
    unreadJumpToPrevious = i18n("access.group.jump_to_prev_unread"),
    unreadJumpToNext = i18n("access.group.jump_to_next_unread"),
    deleteDialogTitle = { isCreator ->
        if (isCreator) i18n("group.dissolve.dialog.title")
        else i18n("group.leave.dialog.title")
    },
    deleteDialogMessage = { isCreator ->
        if (isCreator) i18n("group.dissolve.dialog.message")
        else i18n("group.leave.dialog.message")
    },
    deleteDialogButton = { isCreator ->
        if (isCreator) i18n("group.dissolve.dialog.button")
        else i18n("group.leave.dialog.button")
    },
    messageMaxLength = MAX_GROUP_POST_TEXT_LENGTH,
    messageReplyIntro = i18n("group.message.reply.intro"),
    messageReplyClose = i18n("access.group.reply.close"),
    messageReplyHint = i18n("group.message.reply.hint"),
    messageHint = i18n("group.message.hint"),
    sharingActionTitle = i18n("group.invite.action.title"),
    sharingActionClose = i18n("access.group.invite.action.close"),
    sharingActionNoContacts = i18n("group.invite.action.no_contacts"),
)
