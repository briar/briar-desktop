package org.briarproject.briar.swing.actions;

import org.briarproject.briar.swing.MainUI;

import java.awt.event.ActionEvent;

import de.topobyte.swing.util.action.SimpleAction;

public class CloseChatAction extends SimpleAction {

	private MainUI mainUI;

	public CloseChatAction(MainUI mainUI) {
		super("Close chat window", "Close the current chat window",
				"res/icons/22/system-lock-screen.png");
		this.mainUI = mainUI;
	}

	@Override
	public void actionPerformed(ActionEvent actionEvent) {
		mainUI.closeChat();
	}

}
