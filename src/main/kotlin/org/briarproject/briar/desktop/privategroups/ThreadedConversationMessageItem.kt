package org.briarproject.briar.desktop.privategroups

import org.briarproject.bramble.api.sync.GroupId
import org.briarproject.bramble.api.sync.MessageId
import org.briarproject.briar.api.conversation.ConversationMessageHeader

data class ThreadedConversationMessageItem(
    var text: String? = null,
    override val id: MessageId,
    override val groupId: GroupId,
    override val time: Long,
    override val autoDeleteTimer: Long,
    override val isIncoming: Boolean,
    override var isRead: Boolean,
    override var isSent: Boolean,
    override var isSeen: Boolean,

    // todo: support attachments
    // val attachments: List<AttachmentItem>
) : ThreadedConversationItem() {

    constructor(h: ConversationMessageHeader) :
        this(
            id = h.id,
            groupId = h.groupId,
            time = h.timestamp,
            autoDeleteTimer = h.autoDeleteTimer,
            isRead = h.isRead,
            isSent = h.isSent,
            isSeen = h.isSeen,
            isIncoming = !h.isLocal,
        )

    override fun mark(sent: Boolean, seen: Boolean): ThreadedConversationItem {
        return copy(isSent = sent, isSeen = seen)
    }

    override fun markRead(): ThreadedConversationItem {
        return copy(isRead = true)
    }
}
