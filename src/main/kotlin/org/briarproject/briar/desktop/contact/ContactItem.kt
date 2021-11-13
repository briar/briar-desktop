package org.briarproject.briar.desktop.contact

import org.briarproject.bramble.api.contact.Contact
import org.briarproject.bramble.api.contact.ContactId
import org.briarproject.bramble.api.identity.AuthorId
import org.briarproject.briar.api.client.MessageTracker
import kotlin.math.max

data class ContactItem(
    val contactId: ContactId,
    val authorId: AuthorId,
    val name: String,
    val alias: String?,
    val isConnected: Boolean,
    val isEmpty: Boolean,
    val unread: Int,
    val timestamp: Long
) {

    val displayName = if (alias == null) name else "$alias ($name)"

    constructor(contact: Contact, isConnected: Boolean, groupCount: MessageTracker.GroupCount) :
        this(
            contactId = contact.id,
            authorId = contact.author.id,
            name = contact.author.name,
            alias = contact.alias,
            isConnected = isConnected,
            isEmpty = groupCount.msgCount == 0,
            unread = groupCount.unreadCount,
            timestamp = groupCount.latestMsgTime
        )

    fun updateTimestampAndUnread(timestamp: Long, read: Boolean): ContactItem =
        copy(
            isEmpty = false,
            unread = if (read) unread else unread + 1,
            timestamp = max(timestamp, this.timestamp)
        )

    fun updateIsConnected(c: Boolean): ContactItem {
        return copy(isConnected = c)
    }

    fun updateAlias(a: String?): ContactItem {
        return copy(alias = a)
    }
}
