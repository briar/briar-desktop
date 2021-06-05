package org.briarproject.briar.swing.qr;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class TestDisplayQrCode {

	public static void main(String[] args) throws WriterException, IOException {
		String link =
				"briar://adqgq7kvtwiovnv6ty7wbbdvdh3q3fwbm62e2jynitpki7ojt5mqa";
		Path file = Files.createTempFile("briar-link", ".png");
		System.out.println("Creating file: " + file);

		Map<EncodeHintType, ErrorCorrectionLevel> hints = new HashMap<>();
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.Q);

		BitMatrix matrix = new MultiFormatWriter().encode(
				link, BarcodeFormat.QR_CODE, 400, 400, hints);

		BufferedImage image = MatrixToImageWriter.toBufferedImage(matrix);

		JFrame frame = new JFrame("QR link");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JLabel label = new JLabel(new ImageIcon(image));
		frame.getContentPane().add(label);
		frame.setSize(600, 500);
		frame.setVisible(true);
	}

}
