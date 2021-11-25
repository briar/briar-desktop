package org.briarproject.briar.desktop.contact

import org.briarproject.bramble.api.contact.Contact
import org.briarproject.bramble.api.contact.ContactId
import org.briarproject.bramble.api.identity.AuthorId
import org.briarproject.briar.api.client.MessageTracker
import kotlin.math.max

data class RealContactItem(
    override val contactId: ContactId,
    val authorId: AuthorId,
    val name: String,
    val alias: String?,
    override val isConnected: Boolean,
    override val isEmpty: Boolean,
    override val unread: Int,
    override val timestamp: Long
) : ContactItem {

    override val displayName = if (alias == null) name else "$alias ($name)"

    constructor(contact: Contact, isConnected: Boolean, groupCount: MessageTracker.GroupCount) : this(
        contactId = contact.id,
        authorId = contact.author.id,
        name = contact.author.name,
        alias = contact.alias,
        isConnected = isConnected,
        isEmpty = groupCount.msgCount == 0,
        unread = groupCount.unreadCount,
        timestamp = groupCount.latestMsgTime
    )

    fun updateTimestampAndUnread(timestamp: Long, read: Boolean): RealContactItem =
        copy(
            isEmpty = false,
            unread = if (read) unread else unread + 1,
            timestamp = max(timestamp, this.timestamp)
        )

    fun updateIsConnected(c: Boolean): RealContactItem {
        return copy(isConnected = c)
    }

    fun updateAlias(a: String?): RealContactItem {
        return copy(alias = a)
    }

    fun updateFromMessagesRead(c: Int): RealContactItem =
        copy(unread = unread - c)
}
