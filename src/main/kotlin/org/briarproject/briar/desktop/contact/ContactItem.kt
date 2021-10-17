package org.briarproject.briar.desktop.contact

import org.briarproject.bramble.api.contact.Contact
import org.briarproject.briar.api.client.MessageTracker
import org.briarproject.briar.api.identity.AuthorInfo

data class ContactItem(
    val contact: Contact,
    private val authorInfo: AuthorInfo,
    val isConnected: Boolean,
    private val groupCount: MessageTracker.GroupCount
) {
    val isEmpty = groupCount.msgCount == 0
    val unread = groupCount.unreadCount
    val timestamp = groupCount.latestMsgTime
}
