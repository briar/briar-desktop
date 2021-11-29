package org.briarproject.briar.desktop.conversation

import org.briarproject.bramble.api.sync.GroupId
import org.briarproject.bramble.api.sync.MessageId
import org.briarproject.briar.api.client.SessionId
import org.briarproject.briar.api.conversation.ConversationRequest
import org.briarproject.briar.api.sharing.InvitationRequest
import org.briarproject.briar.api.sharing.Shareable

data class ConversationRequestItem(
    val requestedGroupId: GroupId?,
    val requestType: RequestType,
    val sessionId: SessionId,
    val answered: Boolean,
    val msgText: String,
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

    enum class RequestType {
        INTRODUCTION, FORUM, BLOG, GROUP
    }

    constructor(msgText: String, type: RequestType, r: ConversationRequest<*>) : this(
        requestedGroupId = if (r is InvitationRequest) (r.nameable as Shareable).id else null,
        requestType = type,
        sessionId = r.sessionId,
        answered = r.wasAnswered(),
        msgText = msgText,
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

    val canBeOpened = requestedGroupId != null

    override fun mark(sent: Boolean, seen: Boolean): ConversationItem =
        copy(isSent = sent, isSeen = seen)

    override fun markRead(): ConversationItem =
        copy(isRead = true)

    fun markAnswered(): ConversationItem =
        copy(answered = true)
}
