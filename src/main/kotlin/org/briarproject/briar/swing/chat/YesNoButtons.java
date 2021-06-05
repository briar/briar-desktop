package org.briarproject.briar.swing.chat;

import javax.swing.JButton;

public class YesNoButtons {

	private JButton yes = new JButton("Yes");
	private JButton no = new JButton("No");

	public YesNoButtons() {
		yes.addActionListener(e -> {
			yes();
		});
		no.addActionListener(e -> {
			no();
		});
	}

	public JButton getYes() {
		return yes;
	}

	public JButton getNo() {
		return no;
	}

	public void yes() {
		// TODO: implement
	}

	public void no() {
		// TODO: implement
	}

}
