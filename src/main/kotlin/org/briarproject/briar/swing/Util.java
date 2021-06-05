package org.briarproject.briar.swing;

import java.awt.Color;

import javax.swing.JComponent;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import de.topobyte.swing.util.BorderHelper;

public class Util {

	public static void append(JTextPane textPane, String text, Color c) {
		StyleContext sc = StyleContext.getDefaultStyleContext();
		AttributeSet attributes;
		if (c == null) {
			attributes = sc.getEmptySet();
		} else {
			attributes = sc.addAttribute(SimpleAttributeSet.EMPTY,
					StyleConstants.Foreground, c);
		}
		Document doc = textPane.getDocument();
		int len = doc.getLength();
		try {
			doc.insertString(len, text, attributes);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	public static void defaultRightPadding(
			Iterable<? extends JComponent> components) {
		for (JComponent component : components) {
			defaultRightPadding(component);
		}
	}

	public static void defaultRightPadding(JComponent component) {
		BorderHelper.addEmptyBorder(component, 0, 0, 0, 5);
	}

	public static String bytesForUser(byte[] bytes) {
		StringBuilder strb = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			strb.append(bytes[i]);
			if (i != bytes.length - 1) {
				strb.append(",");
			}
		}
		return strb.toString();
	}

}
