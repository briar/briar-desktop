package org.briarproject.briar.swing.contactlist;

import org.briarproject.bramble.api.contact.Contact;
import org.briarproject.briar.swing.identicons.IdenticonDrawable;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import de.topobyte.awt.util.GridBagConstraintsEditor;

public class ContactListRenderer implements ListCellRenderer<ContactWithMeta> {

	@Override
	public Component getListCellRendererComponent(
			JList<? extends ContactWithMeta> list,
			ContactWithMeta contactWithMeta, int index,
			boolean isSelected, boolean cellHasFocus) {

		Color bg = isSelected ? list.getSelectionBackground() :
				list.getBackground();
		Color fg = isSelected ? list.getSelectionForeground() :
				list.getForeground();

		Contact contact = contactWithMeta.getContact();

		JPanel panel = new JPanel(new GridBagLayout());

		IdenticonDrawable identicon =
				new IdenticonDrawable(contact.getAuthor().getId().getBytes());
		identicon.setPreferredSize(new Dimension(36, 36));
		identicon.setWidthOutline(1.5f);

		JLabel label = new JLabel();
		label.setHorizontalAlignment(SwingConstants.LEFT);
		label.setText(contact.getAuthor().getName());

		JLabel labelAlias = new JLabel();
		labelAlias.setHorizontalAlignment(SwingConstants.LEFT);
		if (contact.getAlias() != null) {
			labelAlias.setText(contact.getAlias());
		}

		JLabel labelStatus = new JLabel(
				Integer.toString(contactWithMeta.getNumUnreadMessages()));

		GridBagConstraints c = new GridBagConstraints();
		GridBagConstraintsEditor ce =
				new GridBagConstraintsEditor(c);

		CompoundBorder border = BorderFactory.createCompoundBorder(
				BorderFactory.createBevelBorder(BevelBorder.LOWERED),
				new EmptyBorder(10, 5, 10, 5));

		panel.setBorder(border);
		ce.anchor(GridBagConstraints.FIRST_LINE_START);

		ce.gridPos(0, 0).gridHeight(2).weight(0, 1);
		panel.add(identicon, c);

		ce.gridPos(1, 0).gridHeight(2).weight(0, 1);
		panel.add(labelStatus, c);
		labelStatus.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 8));

		ce.fill(GridBagConstraints.BOTH);
		ce.gridHeight(1);
		ce.weightX(1);
		ce.gridPos(2, 0);
		panel.add(label, c);

		ce.gridPos(2, 1);
		panel.add(labelAlias, c);

		// Set colors
		label.setForeground(fg);
		labelAlias.setForeground(fg);
		labelStatus.setForeground(fg);
		panel.setBackground(bg);
		panel.setForeground(fg);

		return panel;
	}

}
