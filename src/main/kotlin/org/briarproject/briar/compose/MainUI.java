package org.briarproject.briar.compose;

import org.briarproject.bramble.api.account.AccountManager;
import org.briarproject.bramble.api.contact.Contact;
import org.briarproject.bramble.api.contact.ContactManager;
import org.briarproject.bramble.api.contact.event.ContactAddedEvent;
import org.briarproject.bramble.api.crypto.PasswordStrengthEstimator;
import org.briarproject.bramble.api.db.DbException;
import org.briarproject.bramble.api.event.Event;
import org.briarproject.bramble.api.event.EventBus;
import org.briarproject.bramble.api.event.EventListener;
import org.briarproject.briar.api.conversation.ConversationManager;
import org.briarproject.briar.api.conversation.ConversationMessageHeader;
import org.briarproject.briar.api.introduction.IntroductionManager;
import org.briarproject.briar.api.messaging.MessagingManager;
import org.briarproject.briar.api.messaging.PrivateMessageFactory;
//import org.briarproject.briar.swing.actions.AboutAction;
//import org.briarproject.briar.swing.actions.AddContactAction;
//import org.briarproject.briar.swing.actions.ChangePasswordAction;
//import org.briarproject.briar.swing.actions.CloseChatAction;
//import org.briarproject.briar.swing.actions.QuitAction;
//import org.briarproject.briar.swing.actions.SettingsAction;
//import org.briarproject.briar.swing.chat.Chat;
import org.briarproject.briar.compose.config.Configuration;
//import org.briarproject.briar.swing.contactlist.ContactList;
//import org.briarproject.briar.swing.contactlist.ContactListModel;
//import org.briarproject.briar.swing.contactlist.ContactWithMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Collection;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.UIManager;

//import de.topobyte.awt.util.GridBagConstraintsEditor;

public class MainUI extends JFrame implements EventListener {

	final static Logger logger = LoggerFactory.getLogger(MainUI.class);

	private final BriarService briarService;
	private final AccountManager accountManager;
	private final ContactManager contactManager;
	private final ConversationManager conversationManager;
	private final MessagingManager messagingManager;
	private final IntroductionManager introductionManager;
	private final PrivateMessageFactory privateMessageFactory;
	private final EventBus eventBus;
	private final PasswordStrengthEstimator passwordStrengthEstimator;
	private final Configuration configuration;

	private JPanel mainPanel;
	private JPanel contentPanel;
//	private ContactList contactList;
//	private Chat chat;

//	private ContactListModel contactListModel;

	public MainUI(BriarService briarService,
			AccountManager accountManager,
			ContactManager contactManager,
			ConversationManager conversationManager,
			MessagingManager messagingManager,
			IntroductionManager introductionManager,
			PrivateMessageFactory privateMessageFactory,
			EventBus eventBus,
			PasswordStrengthEstimator passwordStrengthEstimator,
			Configuration configuration) {
		this.briarService = briarService;
		this.accountManager = accountManager;
		this.contactManager = contactManager;
		this.conversationManager = conversationManager;
		this.messagingManager = messagingManager;
		this.introductionManager = introductionManager;
		this.privateMessageFactory = privateMessageFactory;
		this.eventBus = eventBus;
		this.passwordStrengthEstimator = passwordStrengthEstimator;
		this.configuration = configuration;

		String lookAndFeel = configuration.getLookAndFeel();
		if (lookAndFeel != null) {
			try {
				UIManager.setLookAndFeel(lookAndFeel);
			} catch (Exception e) {
				logger.error("error while setting look and feel", e);
			}
		}

		setTitle("Briar Swing");
//		setJMenuBar(new MainMenu(this));

		JToolBar toolbar = new JToolBar();
		toolbar.setFloatable(false);
//		toolbar.add(new AddContactAction(this));
//		toolbar.add(new ChangePasswordAction(this));
//		toolbar.add(new SettingsAction(this));
//		toolbar.add(new CloseChatAction(this));
//		toolbar.add(new QuitAction());
//		toolbar.add(new AboutAction(this));

		mainPanel = new JPanel(new BorderLayout());
		setContentPane(mainPanel);

		contentPanel = new JPanel(new GridBagLayout());
		addComponents();

		mainPanel.add(toolbar, BorderLayout.NORTH);
		mainPanel.add(contentPanel, BorderLayout.CENTER);

//		EventBusConnector eventBusConnector =
//				new EventBusConnector(messagingManager);
//		eventBus.addListener(eventBusConnector);
//		eventBus.addListener(this);
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public AccountManager getAccountManager() {
		return accountManager;
	}

	public ContactManager getContactManager() {
		return contactManager;
	}

	public PasswordStrengthEstimator getPasswordStrengthEstimator() {
		return passwordStrengthEstimator;
	}

	private void addComponents() {
		GridBagConstraints c = new GridBagConstraints();
//		GridBagConstraintsEditor ce =
//				new GridBagConstraintsEditor(c);
//
//		contactList = new ContactList();
//		chat = new Chat(conversationManager, messagingManager,
//				introductionManager, privateMessageFactory, eventBus);
//
//		ce.fill(GridBagConstraints.BOTH);
//
//		ce.gridPos(0, 0);
//		ce.weight(3, 1);
//		contentPanel.add(contactList, c);
//
//		ce.gridPos(1, 0);
//		ce.weight(7, 1);
//		contentPanel.add(chat, c);
//
//		reloadContactList();
//
//		contactList.addContactSelectedListener(this::displayChatHistory);
	}

	public void closeChat() {
//		chat.displayNone();
//		contactList.selectNone();
	}

	private void displayChatHistory(Contact contact) {
//		chat.displayHistory(contact);
	}

	private void tryListContacts() {
		try {
			listContacts();
		} catch (DbException e) {
			e.printStackTrace();
		}
	}

	private void listContacts() throws DbException {
//		Collection<Contact> contacts = contactManager.getContacts();
//		for (Contact contact : contacts) {
//			contactListModel.add(new ContactWithMeta(contact), 0);
//		}
//		for (int i = 0; i < contactListModel.getSize(); i++) {
//			ContactWithMeta entry = contactListModel.getElementAt(i);
//			Contact contact = entry.getContact();
//			logger.debug(
//					String.format("%s (%s)", contact.getAuthor().getName(),
//							contact.getAlias()));
//			Collection<ConversationMessageHeader> messageHeaders =
//					conversationManager.getMessageHeaders(contact.getId());
//			int unread = 0;
//			for (ConversationMessageHeader header : messageHeaders) {
//				if (!header.isRead()) {
//					unread += 1;
//				}
//			}
//			entry.setNumUnreadMessages(unread);
//		}
	}

	@Override
	public void eventOccurred(Event e) {
		if (e instanceof ContactAddedEvent) {
			reloadContactList();
		}
	}

	private void reloadContactList() {
//		contactListModel = new ContactListModel();
//		tryListContacts();
//		contactList.setModel(contactListModel);
	}

}
