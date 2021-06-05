package org.briarproject.briar.swing.dialogs;

import org.briarproject.briar.swing.config.Configuration;
import org.briarproject.briar.swing.config.MiscOptionsPane;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Window;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class SettingsDialog extends JDialog {

	private static final long serialVersionUID = -6673051400374126614L;

	private final MiscOptionsPane miscOptions;

	private final JButton buttonCancel;
	private final JButton buttonOk;

	public SettingsDialog(Window owner, Configuration configuration) {
		super(owner, "Briar Swing Settings");

		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.weighty = 1.0;

		miscOptions = new MiscOptionsPane(configuration);
		JScrollPane jspMisc = new JScrollPane();
		jspMisc.setViewportView(miscOptions);

		add(jspMisc, c);

		JPanel buttons = new JPanel();
		buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));

		JPanel buttonGrid = new JPanel();
		buttonGrid.setLayout(new GridLayout(1, 2));

		buttonCancel = new JButton("Cancel");
		buttonOk = new JButton("Ok");

		buttonGrid.add(buttonCancel);
		buttonGrid.add(buttonOk);

		buttons.add(Box.createHorizontalGlue());
		buttons.add(buttonGrid);

		c.gridy = 1;
		c.weighty = 0.0;
		add(buttons, c);

		buttonCancel.setActionCommand("cancel");
		buttonOk.setActionCommand("ok");
	}

	public JButton getOk() {
		return buttonOk;
	}

	public JButton getCancel() {
		return buttonCancel;
	}

	/**
	 * Set the values of the parameter configuration instance according to the
	 * settings in this GUI.
	 *
	 * @param configuration the configuration whose values to set.
	 */
	public void setValues(Configuration configuration) {
		miscOptions.setValues(configuration);
	}

}