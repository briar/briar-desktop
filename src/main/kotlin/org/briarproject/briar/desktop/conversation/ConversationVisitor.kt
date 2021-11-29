package org.briarproject.briar.desktop.conversation

import mu.KotlinLogging
import org.briarproject.bramble.api.db.DatabaseExecutor
import org.briarproject.bramble.api.db.Transaction
import org.briarproject.bramble.api.sync.MessageId
import org.briarproject.briar.api.blog.BlogInvitationRequest
import org.briarproject.briar.api.blog.BlogInvitationResponse
import org.briarproject.briar.api.conversation.ConversationMessageVisitor
import org.briarproject.briar.api.forum.ForumInvitationRequest
import org.briarproject.briar.api.forum.ForumInvitationResponse
import org.briarproject.briar.api.introduction.IntroductionRequest
import org.briarproject.briar.api.introduction.IntroductionResponse
import org.briarproject.briar.api.messaging.PrivateMessageHeader
import org.briarproject.briar.api.privategroup.invitation.GroupInvitationRequest
import org.briarproject.briar.api.privategroup.invitation.GroupInvitationResponse
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18nF

internal class ConversationVisitor(
    private val contactName: String,
    private val loadMessageText: (MessageId) -> String?
) : ConversationMessageVisitor<ConversationItem?> {

    companion object {
        private val LOG = KotlinLogging.logger {}
    }

    @DatabaseExecutor
    override fun visitPrivateMessageHeader(h: PrivateMessageHeader): ConversationItem {
        val item = ConversationMessageItem(h)
        if (h.hasText()) {
            item.text = loadMessageText(h.id)
        } else {
            LOG.warn { "private message without text" }
        }
        return item
    }

    override fun visitBlogInvitationRequest(r: BlogInvitationRequest): ConversationItem {

        return if (r.isLocal)
            ConversationNoticeItem(
                i18nF("blogs_sharing_invitation_sent", r.name, contactName),
                r
            )
        else
            ConversationRequestItem(
                i18nF("blogs_sharing_invitation_received", contactName, r.name),
                ConversationRequestItem.RequestType.BLOG, r
            )
    }

    override fun visitBlogInvitationResponse(r: BlogInvitationResponse): ConversationItem {
        return if (r.isLocal) {
            val text = when {
                r.wasAccepted() ->
                    i18nF("blogs_sharing_response_accepted_sent", contactName)
                r.isAutoDecline ->
                    i18nF("blogs_sharing_response_declined_auto", contactName)
                else ->
                    i18nF("blogs_sharing_response_declined_sent", contactName)
            }
            ConversationNoticeItem(text, r)
        } else {
            val text = when {
                r.wasAccepted() ->
                    i18nF("blogs_sharing_response_accepted_received", contactName)
                else ->
                    i18nF("blogs_sharing_response_declined_received", contactName)
            }
            ConversationNoticeItem(text, r)
        }
    }

    override fun visitForumInvitationRequest(r: ForumInvitationRequest): ConversationItem {
        return if (r.isLocal)
            ConversationNoticeItem(
                i18nF("forum_invitation_sent", r.name, contactName),
                r
            )
        else
            ConversationRequestItem(
                i18nF("forum_invitation_received", contactName, r.name),
                ConversationRequestItem.RequestType.FORUM, r
            )
    }

    override fun visitForumInvitationResponse(r: ForumInvitationResponse): ConversationItem {
        return if (r.isLocal) {
            val text = when {
                r.wasAccepted() ->
                    i18nF("forum_invitation_response_accepted_sent", contactName)
                r.isAutoDecline ->
                    i18nF("forum_invitation_response_declined_auto", contactName)
                else ->
                    i18nF("forum_invitation_response_declined_sent", contactName)
            }
            ConversationNoticeItem(text, r)
        } else {
            val text = when {
                r.wasAccepted() ->
                    i18nF("forum_invitation_response_accepted_received", contactName)
                else ->
                    i18nF("forum_invitation_response_declined_received", contactName)
            }
            ConversationNoticeItem(text, r)
        }
    }

    override fun visitGroupInvitationRequest(r: GroupInvitationRequest): ConversationItem {
        return if (r.isLocal)
            ConversationNoticeItem(
                i18nF("groups_invitations_invitation_sent", contactName, r.name),
                r
            )
        else
            ConversationRequestItem(
                i18nF("groups_invitations_invitation_received", contactName, r.name),
                ConversationRequestItem.RequestType.GROUP, r
            )
    }

    override fun visitGroupInvitationResponse(r: GroupInvitationResponse): ConversationItem {
        return if (r.isLocal) {
            val text = when {
                r.wasAccepted() ->
                    i18nF("groups_invitations_response_accepted_sent", contactName)
                r.isAutoDecline ->
                    i18nF("groups_invitations_response_declined_auto", contactName)
                else ->
                    i18nF("groups_invitations_response_declined_sent", contactName)
            }
            ConversationNoticeItem(text, r)
        } else {
            val text = when {
                r.wasAccepted() ->
                    i18nF("groups_invitations_response_accepted_received", contactName)
                else ->
                    i18nF("groups_invitations_response_declined_received", contactName)
            }
            ConversationNoticeItem(text, r)
        }
    }

    override fun visitIntroductionRequest(r: IntroductionRequest): ConversationItem {
        // todo: use displayName logic somehow?
        val name = r.nameable.name
        return if (r.isLocal)
            ConversationNoticeItem(
                i18nF("introduction_request_sent", contactName, name),
                r
            )
        else {
            val text = when {
                r.wasAnswered() ->
                    i18nF("introduction_request_answered_received", contactName, name)
                r.isContact ->
                    i18nF("introduction_request_exists_received", contactName, name)
                else ->
                    i18nF("introduction_request_received", contactName, name)
            }
            ConversationRequestItem(
                text,
                ConversationRequestItem.RequestType.INTRODUCTION, r
            )
        }
    }

    override fun visitIntroductionResponse(r: IntroductionResponse): ConversationItem {
        // todo: use displayName logic somehow?
        val name = r.introducedAuthor.name
        return if (r.isLocal) {
            val text = when {
                r.wasAccepted() -> {
                    val suffix = if (r.canSucceed())
                        "\n\n" + i18nF("introduction_response_accepted_sent_info", name)
                    else ""
                    i18nF("introduction_request_answered_received", name) + suffix
                }
                r.isAutoDecline ->
                    i18nF("introduction_response_declined_auto", name)
                else ->
                    i18nF("introduction_response_declined_sent", name)
            }
            ConversationNoticeItem(text, r)
        } else {
            val text = when {
                r.wasAccepted() ->
                    i18nF("introduction_response_accepted_received", contactName, name)
                r.isIntroducer ->
                    i18nF("introduction_response_declined_received", contactName, name)
                else ->
                    i18nF("introduction_response_declined_received_by_introducee", contactName, name)
            }
            ConversationNoticeItem(text, r)
        }
    }
}
