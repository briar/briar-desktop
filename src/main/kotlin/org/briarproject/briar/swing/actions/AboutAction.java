package org.briarproject.briar.swing.actions;

import org.briarproject.briar.swing.MainUI;
import org.briarproject.briar.swing.dialogs.AboutDialog;

import java.awt.event.ActionEvent;

import de.topobyte.swing.util.action.SimpleAction;

public class AboutAction extends SimpleAction {

	private MainUI mainUI;

	public AboutAction(MainUI mainUI) {
		super("About Briar Swing", "Read a bit about this project",
				"res/icons/22/help-browser.png");
		this.mainUI = mainUI;
	}

	@Override
	public void actionPerformed(ActionEvent actionEvent) {
		AboutDialog aboutDialog = new AboutDialog(mainUI);
		aboutDialog.setSize(600, 500);
		aboutDialog.setVisible(true);
		aboutDialog.setLocationRelativeTo(mainUI);
	}

}
