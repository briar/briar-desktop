package org.briarproject.briar.swing.chat;

import org.briarproject.bramble.api.FormatException;
import org.briarproject.bramble.api.contact.Contact;
import org.briarproject.bramble.api.db.DbException;
import org.briarproject.bramble.api.event.Event;
import org.briarproject.bramble.api.event.EventBus;
import org.briarproject.bramble.api.event.EventListener;
import org.briarproject.bramble.api.sync.GroupId;
import org.briarproject.bramble.api.sync.MessageId;
import org.briarproject.bramble.api.sync.event.MessagesSentEvent;
import org.briarproject.briar.api.conversation.ConversationManager;
import org.briarproject.briar.api.conversation.ConversationMessageHeader;
import org.briarproject.briar.api.introduction.IntroductionManager;
import org.briarproject.briar.api.introduction.IntroductionRequest;
import org.briarproject.briar.api.introduction.event.IntroductionRequestReceivedEvent;
import org.briarproject.briar.api.messaging.MessagingManager;
import org.briarproject.briar.api.messaging.PrivateMessage;
import org.briarproject.briar.api.messaging.PrivateMessageFactory;
import org.briarproject.briar.api.messaging.PrivateMessageHeader;
import org.briarproject.briar.api.messaging.event.PrivateMessageReceivedEvent;
import org.briarproject.briar.swing.ConversationMessageHeaderComparator;
import org.briarproject.briar.swing.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;

import de.topobyte.awt.util.GridBagConstraintsEditor;

public class Chat extends JPanel implements EventListener {

	final static Logger logger = LoggerFactory.getLogger(Chat.class);

	private final ConversationManager conversationManager;
	private final MessagingManager messagingManager;
	private final IntroductionManager introductionManager;
	private final PrivateMessageFactory privateMessageFactory;
	private final EventBus eventBus;

	private JTextPane history;
	private JScrollPane jspHistory;
	private JTextField input;
	private JButton button;

	private Contact contact = null;

	private ChatHistoryConversationVisitor chatHistoryConversationVisitor;

	public Chat(
			ConversationManager conversationManager,
			MessagingManager messagingManager,
			IntroductionManager introductionManager,
			PrivateMessageFactory privateMessageFactory,
			EventBus eventBus) {
		this.conversationManager = conversationManager;
		this.messagingManager = messagingManager;
		this.introductionManager = introductionManager;
		this.privateMessageFactory = privateMessageFactory;
		this.eventBus = eventBus;

		history = new JTextPane();
		jspHistory = new JScrollPane(history);
		input = new JTextField();
		button = new JButton("Send");

		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		GridBagConstraintsEditor ce =
				new GridBagConstraintsEditor(c);

		ce.fill(GridBagConstraints.BOTH);

		ce.gridPos(0, 0);
		ce.weight(1, 1);
		add(jspHistory, c);
		history.setEditable(false);
		history.setPreferredSize(new Dimension(-1, -1));
		displayNone();

		ce.fill(GridBagConstraints.HORIZONTAL);

		JPanel inputLine = new JPanel(new GridBagLayout());
		createInputLine(inputLine);
		ce.gridPos(0, 1);
		ce.weightY(0);
		add(inputLine, c);

		button.addActionListener(e -> send());
		input.addActionListener(e -> send());

		chatHistoryConversationVisitor =
				new ChatHistoryConversationVisitor(this);
		eventBus.addListener(this);
	}

	private void send() {
		if (contact == null) {
			return;
		}
		String message = input.getText();
		if (message.isEmpty()) {
			return;
		}
		input.setText("");
		try {
			trySend(message);
		} catch (DbException | FormatException e) {
			e.printStackTrace();
		}
	}

	private void trySend(String message) throws DbException, FormatException {
		GroupId group =
				messagingManager.getConversationId(contact.getId());
		PrivateMessage msg = privateMessageFactory.createPrivateMessage(group,
				System.currentTimeMillis(), message, new ArrayList<>());
		messagingManager.addLocalMessage(msg);
	}

	private void createInputLine(JPanel inputLine) {
		GridBagConstraints c = new GridBagConstraints();
		GridBagConstraintsEditor ce =
				new GridBagConstraintsEditor(c);

		ce.fill(GridBagConstraints.BOTH);

		ce.gridPos(0, 0);
		ce.weightY(0);
		ce.weightX(1);
		inputLine.add(input, c);

		ce.gridPos(1, 0);
		ce.weightX(0);
		inputLine.add(button, c);
	}

	public void displayNone() {
		this.contact = null;
		history.setText("");
		Util.append(history, "This is the chat history", null);
	}

	public void displayHistory(Contact contact) {
		this.contact = contact;
		try {
			tryDisplayHistory(contact);
		} catch (DbException e) {
			history.setText("An error occurred");
		}
	}

	private static final DateTimeFormatter formatter =
			DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

	private void tryDisplayHistory(Contact contact) throws DbException {
		history.setText("");
		List<ConversationMessageHeader> messageHeaders = new ArrayList<>(
				conversationManager.getMessageHeaders(contact.getId()));
		Collections.sort(messageHeaders,
				new ConversationMessageHeaderComparator());
		for (ConversationMessageHeader header : messageHeaders) {
			header.accept(chatHistoryConversationVisitor);
		}
		revalidate();
	}

	void appendMessage(ConversationMessageHeader header) {
		try {
			String messageText =
					messagingManager.getMessageText(header.getId());
			appendMessage(header.isLocal(), header.getTimestamp(), messageText);
		} catch (DbException e) {
			logger.warn("Error while getting message text", e);
		}
	}

	void appendMessage(boolean local, long timestamp, String messageText) {
		String name = local ? "You" : "Other";
		LocalDateTime dateTime = LocalDateTime
				.ofInstant(Instant.ofEpochMilli(timestamp),
						ZoneId.systemDefault());
		String author =
				String.format("%s (%s): ", name, formatter.format(dateTime));
		String message = String.format("%s\n", messageText);
		// TODO: we need to use different colors here that work well with
		// the selected look and feel
		Util.append(history, author, local ? Color.RED : Color.BLUE);
		Util.append(history, message, null);
	}

	@Override
	public void eventOccurred(Event e) {
		try {
			tryHandleEvent(e);
		} catch (DbException ex) {
			logger.warn("Exception while handling message", ex);
		}
	}

	private void tryHandleEvent(Event e) throws DbException {
		if (contact == null) {
			return;
		}
		if (e instanceof MessagesSentEvent) {
			MessagesSentEvent mse = (MessagesSentEvent) e;
			Collection<MessageId> sent = mse.getMessageIds();
			if (mse.getContactId().equals(contact.getId())) {
				for (MessageId mid : sent) {
					try {
						String messageText =
								messagingManager.getMessageText(mid);
						appendMessage(true, System.currentTimeMillis(),
								messageText);
					} catch (DbException exception) {
						logger.info("non-text message sent");
					}
				}
			}
		} else if (e instanceof PrivateMessageReceivedEvent) {
			PrivateMessageReceivedEvent pmre =
					(PrivateMessageReceivedEvent) e;
			if (pmre.getContactId().equals(contact.getId())) {
				PrivateMessageHeader header = pmre.getMessageHeader();
				String messageText =
						messagingManager.getMessageText(header.getId());
				appendMessage(false, header.getTimestamp(), messageText);
			}
		} else if (e instanceof IntroductionRequestReceivedEvent) {
			IntroductionRequestReceivedEvent irre =
					(IntroductionRequestReceivedEvent) e;
			IntroductionRequest ir = irre.getMessageHeader();
			chatHistoryConversationVisitor.visitIntroductionRequest(ir);
		} else {
			logger.warn("Received unhandled event: " +
					e.getClass().getSimpleName());
		}
	}

	public void appendYesNoButtons(IntroductionRequest r) {
		YesNoButtons buttons = new YesNoButtons();
		try {
			ConfirmationButtonUtil
					.add(history, buttons.getYes(), buttons.getNo());
		} catch (BadLocationException e) {
			logger.warn("Unable to add confirmation buttons", e);
		}
		buttons.getYes().addActionListener(e -> {
			respond(r, true);
		});
		buttons.getNo().addActionListener(e -> {
			respond(r, false);
		});
	}

	private void respond(IntroductionRequest r, boolean accept) {
		try {
			introductionManager
					.respondToIntroduction(contact.getId(), r.getSessionId(),
							System.currentTimeMillis(), accept);
		} catch (DbException e) {
			logger.warn("Error while responding to introduction request", e);
		}
	}

}
