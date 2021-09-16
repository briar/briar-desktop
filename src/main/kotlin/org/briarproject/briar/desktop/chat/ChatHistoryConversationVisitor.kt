package org.briarproject.briar.desktop.chat

import org.briarproject.bramble.api.db.DbException
import org.briarproject.briar.api.blog.BlogInvitationRequest
import org.briarproject.briar.api.blog.BlogInvitationResponse
import org.briarproject.briar.api.conversation.ConversationMessageHeader
import org.briarproject.briar.api.conversation.ConversationMessageVisitor
import org.briarproject.briar.api.forum.ForumInvitationRequest
import org.briarproject.briar.api.forum.ForumInvitationResponse
import org.briarproject.briar.api.introduction.IntroductionRequest
import org.briarproject.briar.api.introduction.IntroductionResponse
import org.briarproject.briar.api.messaging.MessagingManager
import org.briarproject.briar.api.messaging.PrivateMessageHeader
import org.briarproject.briar.api.privategroup.invitation.GroupInvitationRequest
import org.briarproject.briar.api.privategroup.invitation.GroupInvitationResponse
import org.slf4j.LoggerFactory

class ChatHistoryConversationVisitor(
    private val chat: Chat,
    private val messagingManager: MessagingManager
) :
    ConversationMessageVisitor<Void?> {

    companion object {
        val logger = LoggerFactory.getLogger(ChatHistoryConversationVisitor::class.java)
    }

    fun appendMessage(header: ConversationMessageHeader) {
        try {
            val messageText = messagingManager.getMessageText(header.id)
            chat.appendMessage(header.isLocal, header.timestamp, messageText)
        } catch (e: DbException) {
            logger.warn("Error while getting message text", e)
        }
    }

    override fun visitPrivateMessageHeader(h: PrivateMessageHeader): Void? {
        appendMessage(h)
        return null
    }

    override fun visitBlogInvitationRequest(r: BlogInvitationRequest): Void? {
        return null
    }

    override fun visitBlogInvitationResponse(r: BlogInvitationResponse): Void? {
        return null
    }

    override fun visitForumInvitationRequest(r: ForumInvitationRequest): Void? {
        return null
    }

    override fun visitForumInvitationResponse(r: ForumInvitationResponse): Void? {
        return null
    }

    override fun visitGroupInvitationRequest(r: GroupInvitationRequest): Void? {
        return null
    }

    override fun visitGroupInvitationResponse(r: GroupInvitationResponse): Void? {
        return null
    }

    override fun visitIntroductionRequest(r: IntroductionRequest): Void? {
        chat.appendMessage(
            r.isLocal, r.timestamp,
            String.format(
                "You received an introduction request! Username: %s, Message: %s",
                r.name, r.text
            )
        )
        if (!r.wasAnswered()) {
            chat.appendMessage(
                r.isLocal, r.timestamp,
                "Do you accept the invitation?"
            )
            // TODO chat.appendYesNoButtons(r);
        }
        return null
    }

    override fun visitIntroductionResponse(r: IntroductionResponse): Void? {
        if (r.wasAccepted()) {
            chat.appendMessage(
                r.isLocal, r.timestamp,
                "You accepted the request"
            )
        } else {
            chat.appendMessage(
                r.isLocal, r.timestamp,
                "You declined the request"
            )
        }
        return null
    }
}
