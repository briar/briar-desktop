package org.briarproject.briar.swing.actions;

import java.awt.event.ActionEvent;

import de.topobyte.swing.util.action.SimpleAction;

public class QuitAction extends SimpleAction {

	public QuitAction() {
		super("Quit", "Exit the application",
				"res/icons/22/system-log-out.png");
	}

	@Override
	public void actionPerformed(ActionEvent actionEvent) {
		System.exit(0);
	}

}
