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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.briarproject.briar.desktop.theme.divider
import org.briarproject.briar.desktop.ui.Constants.COLUMN_WIDTH

@Composable
fun HorizontalDivider(modifier: Modifier = Modifier, color: Color = MaterialTheme.colors.divider) {
    Divider(color = color, thickness = 1.dp, modifier = modifier.fillMaxWidth())
}

@Composable
fun ContactDivider() {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        Divider(color = MaterialTheme.colors.divider, thickness = 1.dp, modifier = Modifier.width(COLUMN_WIDTH - 64.dp))
    }
}
