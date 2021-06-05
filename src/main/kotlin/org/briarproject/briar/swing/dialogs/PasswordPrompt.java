package org.briarproject.briar.swing.dialogs;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

public class PasswordPrompt {

	public static class Result {

		private boolean valid = false;
		private char[] password = null;

		public boolean isValid() {
			return valid;
		}

		public char[] getPassword() {
			return password;
		}

	}

	public static Result promptForPassword() {
		JPanel panel = new JPanel();

		JLabel label = new JLabel("Password:");
		JPasswordField input = new JPasswordField(10);

		panel.add(label);
		panel.add(input);

		JOptionPane optionPane = new JOptionPane(panel,
				JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION) {

			private static final long serialVersionUID = 1L;

			@Override
			public void selectInitialValue() {
				input.requestFocusInWindow();
			}

		};

		optionPane.createDialog(null, "Enter password...").setVisible(true);

		Result result = new Result();

		if (optionPane.getValue() == null) {
			return result;
		}

		if (((int) optionPane.getValue()) == JOptionPane.YES_OPTION) {
			result.password = input.getPassword();
			result.valid = true;
		}

		return result;
	}

}
