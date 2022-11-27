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

package org.briarproject.briar.desktop.contact

import androidx.compose.ui.graphics.ImageBitmap
import org.briarproject.bramble.api.contact.Contact
import org.briarproject.bramble.api.identity.AuthorId
import org.briarproject.briar.api.client.MessageTracker
import org.briarproject.briar.api.identity.AuthorInfo
import org.briarproject.briar.desktop.utils.UiUtils.getContactDisplayName
import kotlin.math.max

data class ContactItem(
    override val idWrapper: RealContactIdWrapper,
    val authorId: AuthorId,
    val trustLevel: AuthorInfo.Status,
    private val name: String,
    val alias: String?,
    val isConnected: Boolean,
    val isEmpty: Boolean,
    val unread: Int,
    override val timestamp: Long,
    val avatar: ImageBitmap?,
) : BaseContactItem {

    override val displayName = getContactDisplayName(name, alias)

    constructor(
        contact: Contact,
        authorInfo: AuthorInfo,
        isConnected: Boolean,
        groupCount: MessageTracker.GroupCount,
        avatar: ImageBitmap?
    ) : this(
        idWrapper = RealContactIdWrapper(contact.id),
        authorId = contact.author.id,
        trustLevel = authorInfo.status,
        name = contact.author.name,
        alias = contact.alias,
        isConnected = isConnected,
        isEmpty = groupCount.msgCount == 0,
        unread = groupCount.unreadCount,
        timestamp = groupCount.latestMsgTime,
        avatar = avatar,
    )

    fun updateTimestampAndUnread(timestamp: Long, read: Boolean) =
        copy(
            isEmpty = false,
            unread = if (read) unread else unread + 1,
            timestamp = max(timestamp, this.timestamp)
        )

    fun updateIsConnected(c: Boolean) =
        copy(isConnected = c)

    fun updateAlias(a: String?) =
        copy(alias = a)

    fun updateFromMessagesRead(c: Int) =
        copy(unread = unread - c)

    fun updateAvatar(avatar: ImageBitmap?) =
        copy(avatar = avatar)
}
