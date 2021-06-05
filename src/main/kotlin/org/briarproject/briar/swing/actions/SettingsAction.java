package org.briarproject.briar.swing.actions;

import com.github.weisj.darklaf.LafManager;

import org.briarproject.briar.swing.MainUI;
import org.briarproject.briar.swing.config.Configuration;
import org.briarproject.briar.swing.config.laf.DarkLafInfo;
import org.briarproject.briar.swing.config.laf.LafInfo;
import org.briarproject.briar.swing.config.laf.LookAndFeels;
import org.briarproject.briar.swing.config.laf.NoLafInfo;
import org.briarproject.briar.swing.config.laf.SystemLafInfo;
import org.briarproject.briar.swing.dialogs.SettingsDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import de.topobyte.swing.util.action.SimpleAction;

public class SettingsAction extends SimpleAction {

	final static Logger logger = LoggerFactory.getLogger(SettingsAction.class);

	private MainUI mainUI;

	public SettingsAction(MainUI mainUI) {
		super("Settings", "Configure this app",
				"res/icons/22/preferences-system.png");
		this.mainUI = mainUI;
	}

	private SettingsDialog dialog;

	@Override
	public void actionPerformed(ActionEvent actionEvent) {
		dialog = new SettingsDialog(mainUI, mainUI.getConfiguration());
		dialog.setSize(600, 500);
		dialog.setVisible(true);
		dialog.setLocationRelativeTo(mainUI);

		dialog.getOk().addActionListener(e -> ok());
		dialog.getCancel().addActionListener(e -> cancel());
	}

	private void ok() {
		Configuration configuration = mainUI.getConfiguration();
		dialog.setValues(configuration);

		String lookAndFeel = configuration.getLookAndFeel();

		setLookAndFeel(lookAndFeel);

		dialog.dispose();
	}

	private void cancel() {
		dialog.dispose();
	}

	private void setLookAndFeel(String lookAndFeel) {
		try {
			for (LafInfo lafInfo : LookAndFeels.AVAILABLE) {
				if (lafInfo.getId().equals(lookAndFeel)) {
					setLookAndFeel(lafInfo);
					break;
				}
			}
		} catch (Exception e) {
			logger.error("error while setting look and feel", e);
		}
	}

	private void setLookAndFeel(LafInfo lafInfo)
			throws ClassNotFoundException, UnsupportedLookAndFeelException,
			InstantiationException, IllegalAccessException {
		boolean needUpdateWindows = true;
		if (lafInfo instanceof NoLafInfo) {
			String lookAndFeel = UIManager.getSystemLookAndFeelClassName();
			UIManager.setLookAndFeel(lookAndFeel);
		} else if (lafInfo instanceof SystemLafInfo) {
			String className = ((SystemLafInfo) lafInfo).getClassName();
			UIManager.setLookAndFeel(className);
		} else if (lafInfo instanceof DarkLafInfo) {
			LafManager.setTheme(((DarkLafInfo) lafInfo).getTheme());
			LafManager.install();
			needUpdateWindows = false;
		}
		if (needUpdateWindows) {
			for (Window window : JFrame.getWindows()) {
				SwingUtilities.updateComponentTreeUI(window);
			}
		}
	}

}
