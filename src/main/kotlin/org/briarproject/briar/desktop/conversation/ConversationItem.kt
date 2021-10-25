package org.briarproject.briar.desktop.conversation

import org.briarproject.bramble.api.sync.GroupId
import org.briarproject.bramble.api.sync.MessageId

sealed class ConversationItem {
    abstract val id: MessageId
    abstract val groupId: GroupId
    abstract val time: Long
    abstract val autoDeleteTimer: Long
    abstract val isIncoming: Boolean

    /**
     * Only useful for incoming messages.
     */
    abstract val isRead: Boolean

    /**
     * Only useful for outgoing messages.
     */
    abstract val isSent: Boolean

    /**
     * Only useful for outgoing messages.
     */
    abstract val isSeen: Boolean

    abstract fun mark(sent: Boolean, seen: Boolean): ConversationItem

    abstract fun markRead(): ConversationItem
}
