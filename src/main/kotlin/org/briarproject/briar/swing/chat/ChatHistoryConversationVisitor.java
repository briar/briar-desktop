package org.briarproject.briar.swing.chat;

import org.briarproject.bramble.api.identity.Author;
import org.briarproject.briar.api.blog.BlogInvitationRequest;
import org.briarproject.briar.api.blog.BlogInvitationResponse;
import org.briarproject.briar.api.conversation.ConversationMessageVisitor;
import org.briarproject.briar.api.forum.ForumInvitationRequest;
import org.briarproject.briar.api.forum.ForumInvitationResponse;
import org.briarproject.briar.api.introduction.IntroductionRequest;
import org.briarproject.briar.api.introduction.IntroductionResponse;
import org.briarproject.briar.api.messaging.PrivateMessageHeader;
import org.briarproject.briar.api.privategroup.invitation.GroupInvitationRequest;
import org.briarproject.briar.api.privategroup.invitation.GroupInvitationResponse;

public class ChatHistoryConversationVisitor
		implements ConversationMessageVisitor<Void> {

	private Chat chat;

	public ChatHistoryConversationVisitor(Chat chat) {
		this.chat = chat;
	}

	@Override
	public Void visitPrivateMessageHeader(PrivateMessageHeader h) {
		chat.appendMessage(h);
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
			chat.appendYesNoButtons(r);
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
