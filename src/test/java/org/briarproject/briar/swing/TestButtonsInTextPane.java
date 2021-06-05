package org.briarproject.briar.swing;

import org.briarproject.briar.swing.chat.ConfirmationButtonUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyledDocument;

public class TestButtonsInTextPane {

	public static void main(String[] args) throws Exception {
		TestButtonsInTextPane test = new TestButtonsInTextPane();
		test.setup();
	}

	private class Buttons {

		public JButton yes = new JButton("Yes");
		public JButton no = new JButton("No");

		public Buttons() {
			yes.addActionListener(e -> {
				removeButtons(this, true);
			});
			no.addActionListener(e -> {
				removeButtons(this, false);
			});
		}

	}

	private JTextPane tp = new JTextPane();
	private List<Buttons> listOfButtons = new ArrayList<>();

	public void setup() throws BadLocationException {
		tp.setEditable(false);
		JScrollPane jsp = new JScrollPane(tp);

		JFrame frame = new JFrame("TextPane with buttons");
		frame.getContentPane().add(jsp);
		frame.setSize(400, 400);

		StyledDocument doc = tp.getStyledDocument();
		DefaultCaret caret = (DefaultCaret) tp.getCaret();
		caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);

		SimpleAttributeSet attr = new SimpleAttributeSet();
		for (int i = 0; i < 20; i++) {
			doc.insertString(doc.getLength(), "A line of text\n", attr);
			Buttons buttons = new Buttons();
			listOfButtons.add(buttons);
			ConfirmationButtonUtil.add(tp, buttons.yes, buttons.no);
		}

		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	private void removeButtons(Buttons buttons, boolean accepted) {
		StyledDocument doc = tp.getStyledDocument();
		List<Integer> positions = new ArrayList<>();

		for (int i = 0; i < doc.getLength(); i++) {
			AbstractDocument.LeafElement element =
					(AbstractDocument.LeafElement) doc.getCharacterElement(i);
			Enumeration<?> attributeNames = element.getAttributeNames();
			while (attributeNames.hasMoreElements()) {
				Object attributeName = attributeNames.nextElement();
				Object value = element.getAttribute(attributeName);
				if (value == buttons.yes) {
					positions.add(i);
				} else if (value == buttons.no) {
					positions.add(i);
				}
			}
		}

		Collections.sort(positions);
		int min = positions.get(0);
		int max = positions.get(positions.size() - 1);
		try {
			doc.remove(min, max - min + 1);
			SimpleAttributeSet attr = new SimpleAttributeSet();
			if (accepted) {
				doc.insertString(min, "you accepted the request", attr);
			} else {
				doc.insertString(min, "you declined the request", attr);
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

}
