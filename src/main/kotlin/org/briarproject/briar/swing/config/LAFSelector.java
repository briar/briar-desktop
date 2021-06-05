package org.briarproject.briar.swing.config;

import org.briarproject.briar.swing.config.laf.LafInfo;
import org.briarproject.briar.swing.config.laf.LookAndFeels;

import javax.swing.JComboBox;

import de.topobyte.swing.util.ElementWrapper;
import de.topobyte.swing.util.combobox.ListComboBoxModel;

public class LAFSelector extends JComboBox<ElementWrapper<LafInfo>> {

	public LAFSelector(Configuration configuration) {
		ListComboBoxModel<LafInfo> model =
				new ListComboBoxModel<LafInfo>(LookAndFeels.AVAILABLE) {

					@Override
					public String toString(LafInfo element) {
						return element.getName();
					}
				};

		setModel(model);

		setEditable(false);

		int index = 0;
		String lookAndFeel = configuration.getLookAndFeel();
		if (lookAndFeel != null) {
			for (int i = 0; i < LookAndFeels.AVAILABLE.size(); i++) {
				LafInfo entry = LookAndFeels.AVAILABLE.get(i);
				if (lookAndFeel.equals(entry.getId())) {
					index = i;
					break;
				}
			}
		}

		setSelectedIndex(index);
	}

	public String getSelectedLookAndFeel() {
		int index = getSelectedIndex();
		if (index < 0) {
			return null;
		}
		LafInfo entry = LookAndFeels.AVAILABLE.get(index);
		if (entry == null) {
			return null;
		}
		return entry.getId();
	}

}