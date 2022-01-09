package org.briarproject.briar.desktop.conversation

import org.briarproject.bramble.api.sync.GroupId
import org.briarproject.bramble.api.sync.MessageId
import org.briarproject.briar.api.conversation.ConversationMessageHeader

data class ConversationMessageItem(
    override var text: String? = null,
    override val id: MessageId,
    override val groupId: GroupId,
    override val time: Long,
    override val autoDeleteTimer: Long,
    override val isIncoming: Boolean,
    override var isRead: Boolean,
    override var isSent: Boolean,
    override var isSeen: Boolean,
    var attachments: List<AttachmentItem> = emptyList(),
) : ConversationItem() {

    constructor(h: ConversationMessageHeader) : this(
        id = h.id,
        groupId = h.groupId,
        time = h.timestamp,
        autoDeleteTimer = h.autoDeleteTimer,
        isRead = h.isRead,
        isSent = h.isSent,
        isSeen = h.isSeen,
        isIncoming = !h.isLocal,
    )

    override fun mark(sent: Boolean, seen: Boolean): ConversationItem =
        copy(isSent = sent, isSeen = seen)

    override fun markRead(): ConversationItem =
        copy(isRead = true)
}
