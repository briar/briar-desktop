package org.briarproject.briar.desktop.contact

import androidx.compose.ui.graphics.ImageBitmap
import org.briarproject.bramble.api.contact.Contact
import org.briarproject.bramble.api.identity.AuthorId
import org.briarproject.briar.api.client.MessageTracker
import org.briarproject.briar.desktop.utils.UiUtils.getContactDisplayName
import kotlin.math.max

data class ContactItem(
    override val idWrapper: RealContactIdWrapper,
    val authorId: AuthorId,
    val name: String,
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
        isConnected: Boolean,
        groupCount: MessageTracker.GroupCount,
        avatar: ImageBitmap?
    ) : this(
        idWrapper = RealContactIdWrapper(contact.id),
        authorId = contact.author.id,
        name = contact.author.name,
        alias = contact.alias,
        isConnected = isConnected,
        isEmpty = groupCount.msgCount == 0,
        unread = groupCount.unreadCount,
        timestamp = groupCount.latestMsgTime,
        avatar = avatar,
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

    fun updateFromMessagesRead(c: Int): ContactItem =
        copy(unread = unread - c)
}
