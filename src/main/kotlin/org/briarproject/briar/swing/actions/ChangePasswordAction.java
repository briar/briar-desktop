package org.briarproject.briar.swing.actions;

import com.github.ajalt.clikt.core.UsageError;

import org.briarproject.bramble.api.crypto.DecryptionException;
import org.briarproject.bramble.api.crypto.PasswordStrengthEstimator;
import org.briarproject.briar.swing.AccountUtil;
import org.briarproject.briar.swing.MainUI;
import org.briarproject.briar.swing.dialogs.UpdatePasswordPrompt;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import de.topobyte.swing.util.action.SimpleAction;

public class ChangePasswordAction extends SimpleAction {

	private MainUI mainUI;

	public ChangePasswordAction(MainUI mainUI) {
		super("Change Password", "Update your account password",
				"res/icons/22/password-preferences.png");
		this.mainUI = mainUI;
	}

	@Override
	public void actionPerformed(ActionEvent actionEvent) {
		UpdatePasswordPrompt.Result result =
				UpdatePasswordPrompt.promptForDetails(mainUI);

		if (!result.isValid()) {
			return;
		}

		PasswordStrengthEstimator passwordStrengthEstimator =
				mainUI.getPasswordStrengthEstimator();
		try {
			AccountUtil.check(passwordStrengthEstimator, result);
		} catch (UsageError e) {
			JOptionPane.showMessageDialog(mainUI, e.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		try {
			mainUI.getAccountManager()
					.changePassword(new String(result.getOldPassword())
							, new String(result.getPassword()));
		} catch (DecryptionException e) {
			JOptionPane.showMessageDialog(mainUI, "Wrong old password",
					"Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		JOptionPane.showMessageDialog(mainUI, "Password updated", "Info",
				JOptionPane.INFORMATION_MESSAGE);
	}

}
