package org.briarproject.briar.swing;

import org.briarproject.briar.swing.actions.AboutAction;
import org.briarproject.briar.swing.actions.AddContactAction;
import org.briarproject.briar.swing.actions.ChangePasswordAction;
import org.briarproject.briar.swing.actions.CloseChatAction;
import org.briarproject.briar.swing.actions.QuitAction;
import org.briarproject.briar.swing.actions.SettingsAction;

import javax.swing.JMenu;
import javax.swing.JMenuBar;

public class MainMenu extends JMenuBar {

	private MainUI mainUI;

	public MainMenu(MainUI mainUI) {
		this.mainUI = mainUI;
		JMenu menuFile = menuFile();
		add(menuFile);
		JMenu menuHelp = menuHelp();
		add(menuHelp);
	}

	private JMenu menuFile() {
		JMenu menu = new JMenu("File");

		menu.add(new AddContactAction(mainUI));
		menu.add(new CloseChatAction(mainUI));
		menu.add(new ChangePasswordAction(mainUI));
		menu.add(new SettingsAction(mainUI));
		menu.add(new QuitAction());

		return menu;
	}

	private JMenu menuHelp() {
		JMenu menu = new JMenu("Help");

		menu.add(new AboutAction(mainUI));

		return menu;
	}

}
