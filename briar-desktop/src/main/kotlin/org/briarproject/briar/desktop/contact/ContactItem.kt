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

package org.briarproject.briar.desktop.contact

import org.briarproject.bramble.api.connection.ConnectionRegistry
import org.briarproject.bramble.api.contact.Contact
import org.briarproject.bramble.api.contact.ContactId
import org.briarproject.bramble.api.db.Transaction
import org.briarproject.bramble.api.identity.AuthorId
import org.briarproject.briar.api.client.MessageTracker
import org.briarproject.briar.api.conversation.ConversationManager
import org.briarproject.briar.api.identity.AuthorInfo
import org.briarproject.briar.api.identity.AuthorManager
import org.briarproject.briar.desktop.utils.UiUtils.getContactDisplayName
import kotlin.math.max

data class ContactItem(
    val id: ContactId,
    val authorId: AuthorId,
    val authorInfo: AuthorInfo,
    private val name: String,
    val alias: String?,
    val isConnected: Boolean,
    val isEmpty: Boolean,
    val unread: Int,
    override val timestamp: Long,
) : ContactListItem {
    override val uniqueId: ByteArray = ByteArray(4) { i -> (id.int shr (i * 8)).toByte() }
    override val displayName = getContactDisplayName(name, alias)
    val trustLevel: AuthorInfo.Status = authorInfo.status

    constructor(
        contact: Contact,
        authorInfo: AuthorInfo,
        isConnected: Boolean,
        groupCount: MessageTracker.GroupCount,
    ) : this(
        id = contact.id,
        authorId = contact.author.id,
        authorInfo = authorInfo,
        name = contact.author.name,
        alias = contact.alias,
        isConnected = isConnected,
        isEmpty = groupCount.msgCount == 0,
        unread = groupCount.unreadCount,
        timestamp = groupCount.latestMsgTime,
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

    fun updateAuthorInfo(authorInfo: AuthorInfo) =
        copy(authorInfo = authorInfo)
}

fun loadContactItem(
    txn: Transaction,
    contact: Contact,
    authorManager: AuthorManager,
    connectionRegistry: ConnectionRegistry,
    conversationManager: ConversationManager,
): ContactItem {
    val authorInfo = authorManager.getAuthorInfo(txn, contact)
    return ContactItem(
        contact = contact,
        authorInfo = authorInfo,
        isConnected = connectionRegistry.isConnected(contact.id),
        groupCount = conversationManager.getGroupCount(txn, contact.id),
    )
}
