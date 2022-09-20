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

import androidx.compose.ui.text.AnnotatedString
import org.briarproject.bramble.api.sync.GroupId
import org.briarproject.briar.api.client.MessageTracker
import org.briarproject.briar.api.forum.Forum
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18nF
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18nP
import org.briarproject.briar.desktop.utils.TimeUtils.getFormattedTimestamp
import org.briarproject.briar.desktop.utils.appendCommaSeparated
import org.briarproject.briar.desktop.utils.buildBlankAnnotatedString

interface GroupItem {
    val id: GroupId
    val name: String
    val msgCount: Int
    val unread: Int
    val timestamp: Long
    val description: AnnotatedString
}

data class ForumItem(
    val forum: Forum,
    override val msgCount: Int,
    override val unread: Int,
    override val timestamp: Long,
) : GroupItem {

    constructor(forum: Forum, groupCount: MessageTracker.GroupCount) : this(
        forum = forum,
        msgCount = groupCount.msgCount,
        unread = groupCount.unreadCount,
        timestamp = groupCount.latestMsgTime,
    )

    override val id: GroupId get() = forum.id
    override val name: String get() = forum.name

    override val description: AnnotatedString
        get() = buildBlankAnnotatedString {
            append(name)
            if (unread > 0) appendCommaSeparated(i18nP("access.forums.unread_count", unread))
            if (msgCount == 0) appendCommaSeparated(i18n("group.card.no_posts"))
            else appendCommaSeparated(
                i18nF(
                    "access.forums.last_message_timestamp",
                    getFormattedTimestamp(timestamp)
                )
            )
        }

    override fun equals(other: Any?): Boolean {
        return other is ForumItem && other.id == id
    }

    override fun hashCode(): Int {
        return forum.hashCode()
    }
}
