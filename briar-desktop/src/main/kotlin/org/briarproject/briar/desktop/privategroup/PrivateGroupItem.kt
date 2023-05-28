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
import org.briarproject.briar.api.identity.AuthorInfo
import org.briarproject.briar.api.privategroup.GroupMessageHeader
import org.briarproject.briar.api.privategroup.PrivateGroup
import org.briarproject.briar.desktop.threadedgroup.ThreadedGroupItem
import org.briarproject.briar.desktop.utils.UiUtils.getContactDisplayName
import kotlin.math.max

data class PrivateGroupItem(
    private val privateGroup: PrivateGroup,
    private val creatorInfo: AuthorInfo,
    override val isDissolved: Boolean,
    override val msgCount: Int,
    override val unread: Int,
    override val timestamp: Long,
) : ThreadedGroupItem {

    constructor(
        privateGroup: PrivateGroup,
        creatorInfo: AuthorInfo,
        isDissolved: Boolean,
        groupCount: MessageTracker.GroupCount,
    ) :
        this(
            privateGroup,
            creatorInfo,
            isDissolved = isDissolved,
            msgCount = groupCount.msgCount,
            unread = groupCount.unreadCount,
            timestamp = groupCount.latestMsgTime
        )

    override val id: GroupId = privateGroup.id
    override val name: String = privateGroup.name
    override val creator: String = getContactDisplayName(privateGroup.creator.name, creatorInfo.alias)

    fun updateOnMessageReceived(header: GroupMessageHeader) =
        copy(
            msgCount = msgCount + 1,
            unread = if (header.isRead) unread else unread + 1,
            timestamp = max(header.timestamp, this.timestamp)
        )

    fun updateOnMessagesRead(num: Int) =
        copy(unread = unread - num)

    fun updateOnDissolve() =
        copy(isDissolved = true)
}
