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

package org.briarproject.briar.desktop.privategroups

import org.briarproject.briar.api.client.MessageTracker
import org.briarproject.briar.api.privategroup.PrivateGroup

data class PrivateGroupItem(
    val privateGroup: PrivateGroup,
    val msgCount: Int,
    val unread: Int,
    val timestamp: Long
) {

    constructor(privateGroup: PrivateGroup, groupCount: MessageTracker.GroupCount) :
        this(
            privateGroup,
            msgCount = groupCount.msgCount,
            unread = groupCount.unreadCount,
            timestamp = groupCount.latestMsgTime
        )
}
