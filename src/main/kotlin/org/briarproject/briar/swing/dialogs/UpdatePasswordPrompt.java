package org.briarproject.briar.swing.dialogs;

import org.briarproject.briar.swing.Util;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Arrays;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

import de.topobyte.awt.util.GridBagConstraintsEditor;

public class UpdatePasswordPrompt {

	public static class Result {

		private boolean valid = false;
		private char[] oldPassword = null;
		private char[] password = null;
		private char[] passwordRepeat = null;

		public boolean isValid() {
			return valid;
		}

		public char[] getOldPassword() {
			return oldPassword;
		}

		public char[] getPassword() {
			return password;
		}

		public char[] getPasswordRepeat() {
			return passwordRepeat;
		}

	}

	public static Result promptForDetails(Component parentComponent) {
		JPanel panel = new JPanel(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		GridBagConstraintsEditor ce =
				new GridBagConstraintsEditor(c);

		JLabel labelOldPassword = new JLabel("Old Password:");
		JPasswordField inputOldPassword = new JPasswordField(10);

		JLabel labelPassword = new JLabel("New Password:");
		JPasswordField inputPassword = new JPasswordField(10);

		JLabel labelPasswordRepeat = new JLabel("Repeat Password:");
		JPasswordField inputPasswordRepeat = new JPasswordField(10);

		Util.defaultRightPadding(
				Arrays.asList(labelOldPassword, labelPassword,
						labelPasswordRepeat));

		ce.anchor(GridBagConstraints.LINE_START);

		ce.gridX(0);
		panel.add(labelOldPassword, c);
		ce.gridX(1);
		panel.add(inputOldPassword, c);

		ce.gridY(1);
		ce.gridX(0);
		panel.add(labelPassword, c);
		ce.gridX(1);
		panel.add(inputPassword, c);

		ce.gridY(2);
		ce.gridX(0);
		panel.add(labelPasswordRepeat, c);
		ce.gridX(1);
		panel.add(inputPasswordRepeat, c);

		JOptionPane optionPane = new JOptionPane(panel,
				JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION) {

			private static final long serialVersionUID = 1L;

			@Override
			public void selectInitialValue() {
				inputOldPassword.requestFocusInWindow();
			}

		};

		JDialog dialog =
				optionPane.createDialog(parentComponent, "Set new password...");
		dialog.setVisible(true);

		Result result = new Result();

		if (optionPane.getValue() == null) {
			return result;
		}

		if (((int) optionPane.getValue()) == JOptionPane.YES_OPTION) {
			result.oldPassword = inputOldPassword.getPassword();
			result.password = inputPassword.getPassword();
			result.passwordRepeat = inputPasswordRepeat.getPassword();

			if (empty(result.oldPassword) || empty(result.password) ||
					empty(result.passwordRepeat)) {
				JOptionPane.showMessageDialog(dialog,
						"Please enter your old and a new password", "Error",
						JOptionPane.ERROR_MESSAGE);
				return result;
			}

			result.valid = true;
		}

		return result;
	}

	private static boolean empty(char[] password) {
		return password.length == 0;
	}

}
