package org.briarproject.briar.desktop.conversation

import org.briarproject.bramble.api.sync.GroupId
import org.briarproject.bramble.api.sync.MessageId
import org.briarproject.briar.api.conversation.ConversationRequest
import org.briarproject.briar.api.conversation.ConversationResponse

data class ConversationNoticeItem(
    val notice: String,
    override var text: String?,
    override val id: MessageId,
    override val groupId: GroupId,
    override val time: Long,
    override val autoDeleteTimer: Long,
    override val isIncoming: Boolean,
    override var isRead: Boolean,
    override var isSent: Boolean,
    override var isSeen: Boolean,
) : ConversationItem() {

    constructor(notice: String, r: ConversationRequest<*>) : this(
        notice = notice,
        text = r.text,
        id = r.id,
        groupId = r.groupId,
        time = r.timestamp,
        autoDeleteTimer = r.autoDeleteTimer,
        isRead = r.isRead,
        isSent = r.isSent,
        isSeen = r.isSeen,
        isIncoming = !r.isLocal,
    )

    constructor(notice: String, r: ConversationResponse) : this(
        notice = notice,
        text = null,
        id = r.id,
        groupId = r.groupId,
        time = r.timestamp,
        autoDeleteTimer = r.autoDeleteTimer,
        isRead = r.isRead,
        isSent = r.isSent,
        isSeen = r.isSeen,
        isIncoming = !r.isLocal,
    )

    override fun mark(sent: Boolean, seen: Boolean): ConversationItem =
        copy(isSent = sent, isSeen = seen)

    override fun markRead(): ConversationItem =
        copy(isRead = true)
}
