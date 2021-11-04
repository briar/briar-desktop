package org.briarproject.briar.desktop.contact

import org.briarproject.bramble.api.contact.Contact
import org.briarproject.briar.api.client.MessageTracker
import org.briarproject.briar.api.conversation.ConversationMessageHeader
import kotlin.math.max

data class ContactItem(
    val contact: Contact,
    val isConnected: Boolean,
    val isEmpty: Boolean,
    val unread: Int,
    val timestamp: Long
) {

    constructor(contact: Contact, isConnected: Boolean, groupCount: MessageTracker.GroupCount) :
        this(
            contact, isConnected,
            isEmpty = groupCount.msgCount == 0,
            unread = groupCount.unreadCount,
            timestamp = groupCount.latestMsgTime
        )

    fun updateFromMessageHeader(h: ConversationMessageHeader): ContactItem {
        return copy(
            isEmpty = false,
            unread = if (h.isRead) unread else unread + 1,
            timestamp = max(h.timestamp, timestamp)
        )
    }

    fun updateIsConnected(c: Boolean): ContactItem {
        return copy(isConnected = c)
    }

    fun updateAlias(a: String?): ContactItem {
        return copy(contact = contact.updateAlias(a))
    }

    private fun Contact.updateAlias(a: String?): Contact {
        return Contact(id, author, localAuthorId, a, handshakePublicKey, isVerified)
    }
}