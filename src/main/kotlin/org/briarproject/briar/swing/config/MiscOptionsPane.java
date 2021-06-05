package org.briarproject.briar.swing.config;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Scrollable;

import de.topobyte.awt.util.GridBagConstraintsEditor;
import de.topobyte.swing.util.BorderHelper;

public class MiscOptionsPane extends JPanel implements Scrollable {

	private LAFSelector lafSelector;

	public MiscOptionsPane(Configuration configuration) {
		setLayout(new GridBagLayout());
		BorderHelper.addEmptyBorder(this, 0, 5, 0, 5);

		lafSelector = new LAFSelector(configuration);

		GridBagConstraints c = new GridBagConstraints();
		GridBagConstraintsEditor ce = new GridBagConstraintsEditor(c);
		ce.fill(GridBagConstraints.BOTH).gridY(0);

		JLabel labelLaf = new JLabel("Look & Feel:");
		JLabel labelTheme = new JLabel("Theme:");
		BorderHelper.addEmptyBorder(labelLaf, 5, 0, 5, 0);
		BorderHelper.addEmptyBorder(labelTheme, 5, 0, 5, 0);

		addAsRow(labelLaf, ce);
		addAsRow(lafSelector, ce);

		// add an empty panel at then end to make UI in GTK LAF less ugly
		addAsRow(new JPanel(), ce);
	}

	private void addAsRow(JComponent component, GridBagConstraintsEditor ce) {
		ce.weightX(1).gridWidth(2).gridX(0);
		add(component, ce.getConstraints());
		ce.getConstraints().gridy++;
	}

	@Override
	public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}

	@Override
	public int getScrollableUnitIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		return 1;
	}

	@Override
	public int getScrollableBlockIncrement(Rectangle visibleRect,
			int orientation, int direction) {
		return 10;
	}

	@Override
	public boolean getScrollableTracksViewportWidth() {
		return true;
	}

	@Override
	public boolean getScrollableTracksViewportHeight() {
		return false;
	}

	/**
	 * Set the values of the parameter configuration instance according to the
	 * settings in this GUI.
	 *
	 * @param configuration the configuration whose values to set.
	 */
	public void setValues(Configuration configuration) {
		configuration.setLookAndFeel(lafSelector.getSelectedLookAndFeel());
	}

}
