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

import org.briarproject.bramble.api.sync.GroupId
import org.briarproject.briar.api.client.MessageTracker
import org.briarproject.briar.api.client.PostHeader
import org.briarproject.briar.api.privategroup.PrivateGroup
import org.briarproject.briar.desktop.group.GroupItem
import kotlin.math.max

data class PrivateGroupItem(
    val privateGroup: PrivateGroup,
    override val msgCount: Int,
    override val unread: Int,
    override val timestamp: Long,
) : GroupItem {

    constructor(privateGroup: PrivateGroup, groupCount: MessageTracker.GroupCount) :
        this(
            privateGroup,
            msgCount = groupCount.msgCount,
            unread = groupCount.unreadCount,
            timestamp = groupCount.latestMsgTime
        )

    override val id: GroupId = privateGroup.id
    override val name: String = privateGroup.name

    fun updateOnPostReceived(header: PostHeader) =
        copy(
            msgCount = msgCount + 1,
            unread = if (header.isRead) unread else unread + 1,
            timestamp = max(header.timestamp, this.timestamp)
        )

    fun updateOnPostsRead(num: Int) =
        copy(unread = unread - num)
}
