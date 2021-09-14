package org.briarproject.briar.desktop.chat;

import org.briarproject.bramble.api.db.DbException;
import org.briarproject.bramble.api.identity.Author;
import org.briarproject.briar.api.blog.BlogInvitationRequest;
import org.briarproject.briar.api.blog.BlogInvitationResponse;
import org.briarproject.briar.api.conversation.ConversationMessageHeader;
import org.briarproject.briar.api.conversation.ConversationMessageVisitor;
import org.briarproject.briar.api.forum.ForumInvitationRequest;
import org.briarproject.briar.api.forum.ForumInvitationResponse;
import org.briarproject.briar.api.introduction.IntroductionRequest;
import org.briarproject.briar.api.introduction.IntroductionResponse;
import org.briarproject.briar.api.messaging.MessagingManager;
import org.briarproject.briar.api.messaging.PrivateMessageHeader;
import org.briarproject.briar.api.privategroup.invitation.GroupInvitationRequest;
import org.briarproject.briar.api.privategroup.invitation.GroupInvitationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChatHistoryConversationVisitor
        implements ConversationMessageVisitor<Void> {

    final static Logger logger = LoggerFactory.getLogger(ChatHistoryConversationVisitor.class);

    private Chat chat;
    private MessagingManager messagingManager;

    public ChatHistoryConversationVisitor(Chat chat, MessagingManager messagingManager) {
        this.chat = chat;
        this.messagingManager = messagingManager;
    }

    void appendMessage(ConversationMessageHeader header) {
        try {
            String messageText = messagingManager.getMessageText(header.getId());
            chat.appendMessage(header.isLocal(), header.getTimestamp(), messageText);
        } catch (DbException e) {
            logger.warn("Error while getting message text", e);
        }
    }

    @Override
    public Void visitPrivateMessageHeader(PrivateMessageHeader h) {
        appendMessage(h);
        return null;
    }

    @Override
    public Void visitBlogInvitationRequest(BlogInvitationRequest r) {
        return null;
    }

    @Override
    public Void visitBlogInvitationResponse(BlogInvitationResponse r) {
        return null;
    }

    @Override
    public Void visitForumInvitationRequest(ForumInvitationRequest r) {
        return null;
    }

    @Override
    public Void visitForumInvitationResponse(ForumInvitationResponse r) {
        return null;
    }

    @Override
    public Void visitGroupInvitationRequest(GroupInvitationRequest r) {
        return null;
    }

    @Override
    public Void visitGroupInvitationResponse(GroupInvitationResponse r) {
        return null;
    }

    @Override
    public Void visitIntroductionRequest(IntroductionRequest r) {
        Author nameable = r.getNameable();
        chat.appendMessage(r.isLocal(), r.getTimestamp(), String.format(
                "You received an introduction request! Username: %s, Message: %s",
                r.getName(), r.getText()));
        if (!r.wasAnswered()) {
            chat.appendMessage(r.isLocal(), r.getTimestamp(),
                    "Do you accept the invitation?");
            // TODO chat.appendYesNoButtons(r);
        }
        return null;
    }

    @Override
    public Void visitIntroductionResponse(IntroductionResponse r) {
        if (r.wasAccepted()) {
            chat.appendMessage(r.isLocal(), r.getTimestamp(),
                    "You accepted the request");
        } else {
            chat.appendMessage(r.isLocal(), r.getTimestamp(),
                    "You declined the request");
        }
        return null;
    }

}
