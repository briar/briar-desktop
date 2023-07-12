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

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.briarproject.briar.desktop.theme.outline

@Composable
fun NumberBadge(num: Int, modifier: Modifier = Modifier) {
    val outlineColor = MaterialTheme.colors.outline
    val briarSecondary = MaterialTheme.colors.secondary
    if (num > 0) {
        Box(
            modifier = modifier
                .height(20.dp)
                .widthIn(min = 20.dp, max = Dp.Infinity)
                .border(1.dp, outlineColor, CircleShape)
                .background(briarSecondary, CircleShape)
                .padding(horizontal = 6.dp)
        ) {
            Text(
                modifier = Modifier.align(Center),
                style = MaterialTheme.typography.overline,
                textAlign = TextAlign.Center,
                text = num.toString(),
                maxLines = 1
            )
        }
    }
}
