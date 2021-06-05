package org.briarproject.briar.swing.contactlist;

import org.briarproject.bramble.api.contact.Contact;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import de.topobyte.awt.util.GridBagConstraintsEditor;

public class ContactList extends JPanel {

	public static interface ContactSelectedListener {

		void contactSelected(Contact contact);

	}

	private JList<ContactWithMeta> list;
	private List<ContactSelectedListener> listeners = new ArrayList<>();

	public ContactList() {
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		GridBagConstraintsEditor ce =
				new GridBagConstraintsEditor(c);

		list = new JList<>();
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane jsp = new JScrollPane(list);
		jsp.setPreferredSize(new Dimension(-1, -1));

		ce.fill(GridBagConstraints.BOTH).weight(1, 1);
		add(jsp, c);

		list.setCellRenderer(new ContactListRenderer());

		list.addListSelectionListener(e -> {
			int selected = list.getSelectedIndex();
			if (selected == -1) {
				return;
			}
			if (e.getValueIsAdjusting()) {
				return;
			}
			ContactWithMeta contact = list.getModel().getElementAt(selected);
			for (ContactSelectedListener listener : listeners) {
				listener.contactSelected(contact.getContact());
			}
		});
	}

	public void setModel(ContactListModel model) {
		list.setModel(model);
	}

	public void selectNone() {
		list.clearSelection();
	}

	public void addContactSelectedListener(ContactSelectedListener listener) {
		listeners.add(listener);
	}

	public void removeContactSelectedListener(
			ContactSelectedListener listener) {
		listeners.remove(listener);
	}

}
