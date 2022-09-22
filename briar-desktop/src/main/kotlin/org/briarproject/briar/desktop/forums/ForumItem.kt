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

import org.briarproject.bramble.api.sync.GroupId
import org.briarproject.briar.api.client.MessageTracker
import org.briarproject.briar.api.forum.Forum

interface GroupItem {
    val id: GroupId
    val name: String
    val msgCount: Int
    val unread: Int
    val timestamp: Long
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

    override fun equals(other: Any?): Boolean {
        return other is ForumItem && other.id == id
    }

    override fun hashCode(): Int {
        return forum.hashCode()
    }
}
