package org.briarproject.briar.swing.actions;

import org.briarproject.bramble.api.FormatException;
import org.briarproject.bramble.api.contact.ContactManager;
import org.briarproject.bramble.api.db.DbException;
import org.briarproject.briar.swing.MainUI;
import org.briarproject.briar.swing.dialogs.AddContactPrompt;

import java.awt.event.ActionEvent;
import java.security.GeneralSecurityException;

import javax.swing.JOptionPane;

import de.topobyte.swing.util.action.SimpleAction;

public class AddContactAction extends SimpleAction {

	private MainUI mainUI;

	public AddContactAction(MainUI mainUI) {
		super("Add Contact", "Add a contact using a briar:// link",
				"res/icons/22/list-add.png");
		this.mainUI = mainUI;
	}

	@Override
	public void actionPerformed(ActionEvent actionEvent) {
		String ownLink = null;

		ContactManager contactManager = mainUI.getContactManager();
		try {
			ownLink = contactManager.getHandshakeLink();
		} catch (DbException e) {
			e.printStackTrace();
			return;
		}

		AddContactPrompt.Result result =
				AddContactPrompt.promptForLink(mainUI, ownLink);
		if (!result.isValid()) {
			return;
		}

		try {
			contactManager
					.addPendingContact(result.getLink(), result.getAlias());
		} catch (DbException | FormatException | GeneralSecurityException e) {
			e.printStackTrace();
		}

		JOptionPane.showMessageDialog(mainUI, "Contact added", "Info",
				JOptionPane.INFORMATION_MESSAGE);
	}

}
