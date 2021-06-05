package org.briarproject.briar.swing.identicons;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import de.topobyte.awt.util.GridBagConstraintsEditor;
import de.topobyte.swing.util.BorderHelper;

public class TestNineIdenticons {

	public static void main(String[] args) {
		// A bunch of generated author ids
		byte[][] authorIds = {
				new byte[] {-111, 66, 31, -104, 71, -20, 85, 41, 56, 99, -112,
						-83, 93, 1, -70, -72, 21, -55, 10, 67, 47, 99, 34, -115,
						39, 73, -98, -87, 47, 75, -67, 8},
				new byte[] {-59, 85, 84, -15, 84, -119, 41, -14, -8, 37, -26,
						-119, -6, 28, -70, -10, -56, 111, -56, -2, -116, -12,
						-3, 119, -75, 72, 49, 56, -96, 21, 13, -117},
				new byte[] {-69, -39, -40, 22, 124, 50, 86, 33, -55, 14, 86,
						-15, 44, -43, -48, -9, -74, 60, 71, 126, 100, -118, -45,
						-123, 91, -3, 112, -86, 104, -95, 10, -91},
				new byte[] {120, -42, -87, 97, 120, -84, -66, -15, 2, -111,
						-114, -119, 29, -57, 122, 34, 54, 100, 127, 78, 37, -30,
						88, -92, 89, 14, 67, 75, 125, 100, -86, -15},
				new byte[] {64, 92, -93, 23, 113, -90, 78, 108, 73, -52, -7,
						-68, 100, 37, -2, 19, -27, 2, -3, -23, 105, 29, 110, 55,
						-33, 108, -82, 119, -83, 31, -61, 106},
				new byte[] {99, -106, 14, -81, -59, 59, 99, 95, -2, 18, 32, 120,
						-61, 112, -15, 30, -66, 124, -116, -78, 112, 52, -48,
						-95, -66, -105, -55, 55, 51, 56, 104, -128},
				new byte[] {30, 110, -29, -88, -66, 68, -98, 105, 63, 89, -71,
						-128, 67, -117, -77, 96, 75, -22, 117, 84, 77, -122, 83,
						-21, 89, -27, -98, 78, -70, -35, 114, 16},
				new byte[] {16, -112, -120, -121, 76, 28, 18, 42, 46, 64, 81,
						15, -5, -45, 120, 94, 75, -35, 46, 80, 126, -5, -102,
						84, 47, 42, 51, -27, -43, 7, 67, -16},
				new byte[] {82, 26, 45, -38, 10, -3, 53, 67, -5, -59, -109, 122,
						-59, -92, 71, -21, 126, -42, 37, -53, 101, -118, -24,
						-11, -5, 90, -25, -75, 100, 89, 127, 94}
		};

		JFrame frame = new JFrame();
		frame.setTitle("Identicons");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel panel = new JPanel(new GridBagLayout());
		frame.setContentPane(panel);

		GridBagConstraintsEditor ce = new GridBagConstraintsEditor();
		ce.fill(GridBagConstraints.BOTH);
		ce.weight(1, 1);
		for (int r = 0; r < 3; r++) {
			for (int c = 0; c < 3; c++) {
				ce.gridPos(r, c);
				int i = r * 3 + c;
				IdenticonDrawable identicon =
						new IdenticonDrawable(authorIds[i]);
				identicon.setWidthOutline(1.5f);
				BorderHelper.addEmptyBorder(identicon, 2, 2, 2, 2);
				panel.add(identicon, ce.getConstraints());
			}
		}

		frame.setVisible(true);
		frame.setSize(500, 500);
	}

}
