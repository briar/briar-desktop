package org.briarproject.briar.swing.dialogs;

import org.briarproject.briar.swing.Util;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Arrays;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import de.topobyte.awt.util.GridBagConstraintsEditor;

public class NewAccountPrompt {

	public static class Result {

		private boolean valid = false;
		private String nickname = null;
		private char[] password = null;
		private char[] passwordRepeat = null;

		public boolean isValid() {
			return valid;
		}

		public String getNickname() {
			return nickname;
		}

		public char[] getPassword() {
			return password;
		}

		public char[] getPasswordRepeat() {
			return passwordRepeat;
		}

	}

	public static Result promptForDetails() {
		JPanel panel = new JPanel(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		GridBagConstraintsEditor ce =
				new GridBagConstraintsEditor(c);

		JLabel labelNick = new JLabel("Nickname:");
		JTextField inputNick = new JTextField(10);

		JLabel labelPassword = new JLabel("Password:");
		JPasswordField inputPassword = new JPasswordField(10);

		JLabel labelPasswordRepeat = new JLabel("Repeat Password:");
		JPasswordField inputPasswordRepeat = new JPasswordField(10);

		Util.defaultRightPadding(
				Arrays.asList(labelNick, labelPassword, labelPasswordRepeat));

		ce.anchor(GridBagConstraints.LINE_START);

		ce.gridX(0);
		panel.add(labelNick, c);
		ce.gridX(1);
		panel.add(inputNick, c);

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
				inputNick.requestFocusInWindow();
			}

		};

		optionPane.createDialog(null, "Create new account...").setVisible(true);

		Result result = new Result();

		if (optionPane.getValue() == null) {
			return result;
		}

		if (((int) optionPane.getValue()) == JOptionPane.YES_OPTION) {
			result.nickname = inputNick.getText();
			result.password = inputPassword.getPassword();
			result.passwordRepeat = inputPasswordRepeat.getPassword();
			result.valid = true;
		}

		return result;
	}

}
