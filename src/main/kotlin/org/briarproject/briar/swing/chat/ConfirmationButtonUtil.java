package org.briarproject.briar.swing.chat;

import javax.swing.JButton;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;

public class ConfirmationButtonUtil {

	public static void add(JTextPane tp, JButton yes, JButton no)
			throws BadLocationException {
		Document doc = tp.getDocument();

		tp.setCaretPosition(doc.getLength());
		tp.insertComponent(yes);
		tp.setCaretPosition(doc.getLength());
		tp.insertComponent(no);

		SimpleAttributeSet attr = new SimpleAttributeSet();
		doc.insertString(doc.getLength(), "\n", attr);
	}

}
