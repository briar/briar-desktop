package org.briarproject.briar.swing.dialogs;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import org.briarproject.briar.swing.Util;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import de.topobyte.awt.util.GridBagConstraintsEditor;

public class AddContactPrompt {

	public static class Result {

		private boolean valid = false;
		private String link = null;
		private String alias = null;

		public boolean isValid() {
			return valid;
		}

		public String getLink() {
			return link;
		}

		public String getAlias() {
			return alias;
		}

	}

	public static Result promptForLink(Component parentComponent,
			String ownLink) {
		JPanel panel = new JPanel(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		GridBagConstraintsEditor ce =
				new GridBagConstraintsEditor(c);

		JLabel labelContactLink = new JLabel("Contact link:");
		JTextField inputContactLink = new JTextField(40);

		JLabel labelContactName = new JLabel("Contact name:");
		JTextField inputContactName = new JTextField(40);

		JLabel labelYourLink = new JLabel("Your link:");
		JTextField inputYourLink = new JTextField(40);
		inputYourLink.setText(ownLink);

		JLabel labelQrCode = new JLabel("QR code:");

		Map<EncodeHintType, ErrorCorrectionLevel> hints = new HashMap<>();
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.Q);

		JLabel qrLabel;
		try {
			BitMatrix matrix = new MultiFormatWriter().encode(
					ownLink, BarcodeFormat.QR_CODE, 400, 400, hints);
			BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(matrix);
			qrLabel = new JLabel(new ImageIcon(qrImage));
		} catch (WriterException e) {
			qrLabel = new JLabel(
					"Error while creating QR code: " + e.getMessage());
		}

		Util.defaultRightPadding(
				Arrays.asList(labelContactLink, labelContactName, labelYourLink,
						labelQrCode));

		ce.anchor(GridBagConstraints.FIRST_LINE_START);

		int gridy = 0;

		ce.gridY(gridy++);
		ce.gridX(0);
		panel.add(labelContactLink, c);
		ce.gridX(1);
		panel.add(inputContactLink, c);

		ce.gridY(gridy++);
		ce.gridX(0);
		panel.add(labelContactName, c);
		ce.gridX(1);
		panel.add(inputContactName, c);

		ce.gridY(gridy++);
		ce.gridX(0);
		panel.add(labelYourLink, c);
		ce.gridX(1);
		panel.add(inputYourLink, c);

		ce.gridY(gridy++);
		ce.gridX(0);
		panel.add(labelQrCode, c);
		ce.gridX(1);
		panel.add(qrLabel, c);

		JOptionPane optionPane = new JOptionPane(panel,
				JOptionPane.QUESTION_MESSAGE, JOptionPane.OK_CANCEL_OPTION) {

			private static final long serialVersionUID = 1L;

			@Override
			public void selectInitialValue() {
				inputContactLink.requestFocusInWindow();
			}

		};

		JDialog dialog =
				optionPane.createDialog(parentComponent, "Add contact...");
		dialog.setVisible(true);

		Result result = new Result();

		if (optionPane.getValue() == null) {
			return result;
		}

		if (((int) optionPane.getValue()) == JOptionPane.YES_OPTION) {
			result.link = inputContactLink.getText();
			result.alias = inputContactName.getText();

			if (result.link.isEmpty()) {
				JOptionPane.showMessageDialog(dialog,
						"Please enter a contact link", "Error",
						JOptionPane.ERROR_MESSAGE);
				return result;
			}

			if (result.alias.isEmpty()) {
				JOptionPane.showMessageDialog(dialog,
						"Please enter a contact name", "Error",
						JOptionPane.ERROR_MESSAGE);
				return result;
			}

			result.valid = true;
		}

		return result;
	}

}
