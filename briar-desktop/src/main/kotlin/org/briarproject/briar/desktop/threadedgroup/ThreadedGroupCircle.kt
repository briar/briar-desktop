/*
 * Briar Desktop
 * Copyright (C) 2021-2023 The Briar Project
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

package org.briarproject.briar.desktop.threadedgroup

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.briarproject.bramble.api.sync.GroupId
import org.briarproject.briar.desktop.theme.outline
import org.briarproject.briar.desktop.utils.InternationalizationUtils.locale

@Composable
fun ThreadedGroupCircle(item: ThreadedGroupItem, modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Center,
        modifier = modifier
            .size(36.dp)
            .clip(CircleShape)
            .border(1.dp, MaterialTheme.colors.outline, CircleShape)
            .background(item.id.getBackgroundColor()),
    ) {
        Text(
            text = item.name.substring(0..0).uppercase(locale),
            color = Color.White,
            style = MaterialTheme.typography.body1.copy(
                fontSize = 24.sp,
                shadow = Shadow(
                    color = Color.Black.copy(alpha = 0.4f),
                    offset = Offset(4f, 4f),
                    blurRadius = 8f
                )
            )
        )
    }
}

private fun GroupId.getBackgroundColor(): Color {
    return Color(
        red = bytes.getByte(0) * 3 / 4 + 96,
        green = bytes.getByte(1) * 3 / 4 + 96,
        blue = bytes.getByte(2) * 3 / 4 + 96,
    )
}

private fun ByteArray.getByte(index: Int): Byte {
    return this[index % size]
}
