/*
 * Briar Desktop
 * Copyright (C) 2021-2022 The Briar Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.briarproject.briar.desktop.ui

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.compositeOver

/**
 * A surface that is meant to be used as background, with [MaterialTheme.colors.background] as default color.
 * It will automatically match the corresponding [contentColor] if the given [color] is part of the Material theme.
 * An [overlayAlpha] value bigger than zero applies a color overlay to the background color,
 * that can be used to generate slightly varying colors for different parts of the UI.
 */
@Composable
fun BackgroundSurface(
    modifier: Modifier = Modifier,
    shape: Shape = RectangleShape,
    overlayAlpha: Float = 0f,
    color: Color = MaterialTheme.colors.background,
    contentColor: Color = contentColorFor(color),
    content: @Composable () -> Unit
) = Surface(
    modifier = modifier,
    shape = shape,
    color = if (overlayAlpha != 0f) contentColor.copy(alpha = overlayAlpha).compositeOver(color) else color,
    contentColor = contentColor,
    content = content
)
