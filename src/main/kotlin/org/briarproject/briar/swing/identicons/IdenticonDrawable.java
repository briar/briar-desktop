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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.geom.Ellipse2D;

import javax.swing.JComponent;

import de.topobyte.awt.util.GraphicsUtil;

public class IdenticonDrawable extends JComponent {

	private float widthOutline = 1;
	private final Identicon identicon;

	public IdenticonDrawable(byte[] input) {
		identicon = new Identicon(input);
	}

	public void setWidthOutline(float widthOutline) {
		this.widthOutline = widthOutline;
	}

	@Override
	public void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);

		Insets insets = getInsets();
		int width = getWidth() - insets.left - insets.right;
		int height = getHeight() - insets.top - insets.bottom;
		identicon.setOffset(insets.left, insets.top);
		identicon.updateSize(width, height);

		Graphics2D g = (Graphics2D) graphics;
		GraphicsUtil.useAntialiasing(g, true);

		Ellipse2D clip = new Ellipse2D.Double(
				insets.left, insets.top, width, height);
		g.clip(clip);

		identicon.draw(g);

		g.setClip(null);
		g.setColor(Color.GRAY);
		g.setStroke(new BasicStroke(widthOutline));
		g.draw(clip);
	}

}
