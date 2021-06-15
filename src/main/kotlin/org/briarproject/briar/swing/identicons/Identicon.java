package org.briarproject.briar.swing.identicons;

/**
 * Copyright 2014 www.delight.im <info@delight.im>
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import de.topobyte.chromaticity.AwtColors;
import de.topobyte.chromaticity.ColorCode;

class Identicon {

	private static final int ROWS = 9, COLUMNS = 9;
	private static final int CENTER_COLUMN_INDEX = COLUMNS / 2 + COLUMNS % 2;

	private final byte[] input;
	private final ColorCode[][] colors;

	private int w;
	private int h;
	private int left;
	private int top;
	private int cellWidth;
	private int cellHeight;

	Identicon(byte[] input) {
		if (input.length == 0) throw new IllegalArgumentException();
		this.input = input;

		colors = new ColorCode[ROWS][COLUMNS];
		ColorCode colorVisible = getForegroundColor();
		ColorCode colorInvisible = getBackgroundColor();

		for (int r = 0; r < ROWS; r++) {
			for (int c = 0; c < COLUMNS; c++) {
				if (isCellVisible(r, c)) {
					colors[r][c] = colorVisible;
				} else {
					colors[r][c] = colorInvisible;
				}
			}
		}
	}

	private byte getByte(int index) {
		return input[index % input.length];
	}

	private boolean isCellVisible(int row, int column) {
		return getByte(3 + row * CENTER_COLUMN_INDEX +
				getSymmetricColumnIndex(column)) >= 0;
	}

	private int getSymmetricColumnIndex(int index) {
		if (index < CENTER_COLUMN_INDEX) return index;
		else return COLUMNS - index - 1;
	}

	private ColorCode getForegroundColor() {
		int r = getByte(0) * 3 / 4 + 96;
		int g = getByte(1) * 3 / 4 + 96;
		int b = getByte(2) * 3 / 4 + 96;
		return new ColorCode(r, g, b);
	}

	private ColorCode getBackgroundColor() {
		// http://www.google.com/design/spec/style/color.html#color-themes
		return new ColorCode(0xFA, 0xFA, 0xFA);
	}

	public void setOffset(int left, int top) {
		this.left = left;
		this.top = top;
	}

	void updateSize(int w, int h) {
		this.w = w;
		this.h = h;
		cellWidth = w / COLUMNS;
		cellHeight = h / ROWS;
	}

	void draw(Graphics2D g) {
		for (int r = 0; r < ROWS; r++) {
			for (int c = 0; c < COLUMNS; c++) {
				int x = cellWidth * c;
				int y = cellHeight * r;
				g.setColor(AwtColors.convert(colors[r][c]));
				Rectangle2D rect = new Rectangle2D.Double(
						left + x, top + y, cellWidth, cellHeight);
				g.fill(rect);
			}
		}
	}
}
