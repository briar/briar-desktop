/*
 * Briar Desktop
 * Copyright (C) 2021-2022 The Briar Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.briarproject.briar.desktop.conversation

import mu.KotlinLogging
import org.briarproject.bramble.api.db.DatabaseExecutor
import org.briarproject.bramble.api.db.DbException
import org.briarproject.bramble.api.db.Transaction
import org.briarproject.bramble.api.sync.MessageId
import org.briarproject.briar.api.attachment.AttachmentReader
import org.briarproject.briar.api.blog.BlogInvitationRequest
import org.briarproject.briar.api.blog.BlogInvitationResponse
import org.briarproject.briar.api.conversation.ConversationMessageVisitor
import org.briarproject.briar.api.forum.ForumInvitationRequest
import org.briarproject.briar.api.forum.ForumInvitationResponse
import org.briarproject.briar.api.introduction.IntroductionRequest
import org.briarproject.briar.api.introduction.IntroductionResponse
import org.briarproject.briar.api.messaging.MessagingManager
import org.briarproject.briar.api.messaging.PrivateMessageHeader
import org.briarproject.briar.api.privategroup.invitation.GroupInvitationRequest
import org.briarproject.briar.api.privategroup.invitation.GroupInvitationResponse
import org.briarproject.briar.desktop.DesktopFeatureFlags
import org.briarproject.briar.desktop.utils.ImageUtils
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18nF
import org.briarproject.briar.desktop.utils.KLoggerUtils.w
import org.briarproject.briar.desktop.utils.UiUtils.getContactDisplayName

internal class ConversationVisitor(
    private val contactName: String,
    private val messagingManager: MessagingManager,
    private val attachmentReader: AttachmentReader,
    private val desktopFeatureFlags: DesktopFeatureFlags,
    private val txn: Transaction,
) : ConversationMessageVisitor<ConversationItem?> {

    companion object {
        private val LOG = KotlinLogging.logger {}
    }

    // todo: implement some message cache similar to Briar Android
    private fun loadMessageText(txn: Transaction, m: MessageId): String? {
        try {
            return messagingManager.getMessageText(txn, m)
        } catch (e: DbException) {
            LOG.w(e) {}
        }
        return null
    }

    @DatabaseExecutor
    override fun visitPrivateMessageHeader(h: PrivateMessageHeader): ConversationItem {
        val item = ConversationMessageItem(h)
        item.attachments = buildList {
            for (header in h.attachmentHeaders) {
                ImageUtils.loadImage(txn, attachmentReader, header).also {
                    add(AttachmentItem(it))
                }
            }
        }
        if (h.hasText()) {
            item.text = loadMessageText(txn, h.id)
        } else {
            LOG.w { "private message without text" }
        }
        return item
    }

    override fun visitBlogInvitationRequest(r: BlogInvitationRequest): ConversationItem {
        return if (r.isLocal)
            ConversationNoticeItem(
                i18nF("blog.invitation.sent", r.name, contactName),
                r
            )
        else {
            val notice = i18nF("blog.invitation.received", contactName, r.name)
            if (desktopFeatureFlags.shouldEnableBlogs())
                ConversationRequestItem(
                    notice,
                    ConversationRequestItem.RequestType.BLOG, r
                )
            else
                ConversationNoticeItem(
                    notice + "\n" + i18n("unsupported_feature"),
                    r
                )
        }
    }

    override fun visitBlogInvitationResponse(r: BlogInvitationResponse): ConversationItem {
        return if (r.isLocal) {
            val notice = when {
                r.wasAccepted() ->
                    i18nF("blog.invitation.response.accepted.sent", contactName)
                r.isAutoDecline ->
                    i18nF("blog.invitation.response.declined.auto", contactName)
                else ->
                    i18nF("blog.invitation.response.declined.sent", contactName)
            }
            ConversationNoticeItem(notice, r)
        } else {
            val notice = when {
                r.wasAccepted() ->
                    i18nF("blog.invitation.response.accepted.received", contactName)
                else ->
                    i18nF("blog.invitation.response.declined.received", contactName)
            }
            ConversationNoticeItem(notice, r)
        }
    }

    override fun visitForumInvitationRequest(r: ForumInvitationRequest): ConversationItem {
        return if (r.isLocal)
            ConversationNoticeItem(
                i18nF("forum.invitation.sent", r.name, contactName),
                r
            )
        else {
            val notice = i18nF("forum.invitation.received", contactName, r.name)
            if (desktopFeatureFlags.shouldEnableForums())
                ConversationRequestItem(
                    notice,
                    ConversationRequestItem.RequestType.FORUM, r
                )
            else
                ConversationNoticeItem(
                    notice + "\n" + i18n("unsupported_feature"),
                    r
                )
        }
    }

    override fun visitForumInvitationResponse(r: ForumInvitationResponse): ConversationItem {
        return if (r.isLocal) {
            val notice = when {
                r.wasAccepted() ->
                    i18nF("forum.invitation.response.accepted.sent", contactName)
                r.isAutoDecline ->
                    i18nF("forum.invitation.response.declined.auto", contactName)
                else ->
                    i18nF("forum.invitation.response.declined.sent", contactName)
            }
            ConversationNoticeItem(notice, r)
        } else {
            val notice = when {
                r.wasAccepted() ->
                    i18nF("forum.invitation.response.accepted.received", contactName)
                else ->
                    i18nF("forum.invitation.response.declined.received", contactName)
            }
            ConversationNoticeItem(notice, r)
        }
    }

    override fun visitGroupInvitationRequest(r: GroupInvitationRequest): ConversationItem {
        return if (r.isLocal)
            ConversationNoticeItem(
                i18nF("group.invitation.sent", contactName, r.name),
                r
            )
        else {
            val notice = i18nF("group.invitation.received", contactName, r.name)
            if (desktopFeatureFlags.shouldEnablePrivateGroups())
                ConversationRequestItem(
                    notice,
                    ConversationRequestItem.RequestType.GROUP, r
                )
            else
                ConversationNoticeItem(
                    notice + "\n" + i18n("unsupported_feature"),
                    r
                )
        }
    }

    override fun visitGroupInvitationResponse(r: GroupInvitationResponse): ConversationItem {
        return if (r.isLocal) {
            val notice = when {
                r.wasAccepted() ->
                    i18nF("group.invitation.response.accepted.sent", contactName)
                r.isAutoDecline ->
                    i18nF("group.invitation.response.declined.auto", contactName)
                else ->
                    i18nF("group.invitation.response.declined.sent", contactName)
            }
            ConversationNoticeItem(notice, r)
        } else {
            val notice = when {
                r.wasAccepted() ->
                    i18nF("group.invitation.response.accepted.received", contactName)
                else ->
                    i18nF("group.invitation.response.declined.received", contactName)
            }
            ConversationNoticeItem(notice, r)
        }
    }

    override fun visitIntroductionRequest(r: IntroductionRequest): ConversationItem {
        val name = getContactDisplayName(r.nameable.name, r.alias)
        return if (r.isLocal)
            ConversationNoticeItem(
                i18nF("introduction.request.sent", contactName, name),
                r
            )
        else {
            val notice = when {
                r.wasAnswered() ->
                    i18nF("introduction.request.answered.received", contactName, name)
                r.isContact ->
                    i18nF("introduction.request.exists.received", contactName, name)
                else ->
                    i18nF("introduction.request.received", contactName, name)
            }
            ConversationRequestItem(
                notice,
                ConversationRequestItem.RequestType.INTRODUCTION, r
            )
        }
    }

    override fun visitIntroductionResponse(r: IntroductionResponse): ConversationItem {
        val name = getContactDisplayName(r.introducedAuthor.name, r.introducedAuthorInfo.alias)
        return if (r.isLocal) {
            val notice = when {
                r.wasAccepted() -> {
                    val suffix = if (r.canSucceed())
                        "\n\n" + i18nF("introduction.response.accepted.sent.info", name)
                    else ""
                    i18nF("introduction.response.accepted.sent", name) + suffix
                }
                r.isAutoDecline ->
                    i18nF("introduction.response.declined.auto", name)
                else ->
                    i18nF("introduction.response.declined.sent", name)
            }
            ConversationNoticeItem(notice, r)
        } else {
            val notice = when {
                r.wasAccepted() ->
                    i18nF("introduction.response.accepted.received", contactName, name)
                r.isIntroducer ->
                    i18nF("introduction.response.declined.received", contactName, name)
                else ->
                    i18nF("introduction.response.declined.received_by_introducee", contactName, name)
            }
            ConversationNoticeItem(notice, r)
        }
    }
}
