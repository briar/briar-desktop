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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.briarproject.briar.desktop.theme.TopAppBar
import org.briarproject.briar.desktop.theme.divider
import org.briarproject.briar.desktop.ui.Constants.HEADER_SIZE

@Composable
fun VerticalDivider() {
    Divider(color = MaterialTheme.colors.divider, modifier = Modifier.fillMaxHeight().width(1.dp))
}

@Composable
fun TopAppBarDivider(topColor: Color = TopAppBar) {
    Column(Modifier.fillMaxHeight()) {
        Divider(color = topColor, modifier = Modifier.height(HEADER_SIZE).width(1.dp))
        Divider(color = MaterialTheme.colors.divider, modifier = Modifier.fillMaxHeight().width(1.dp))
    }
}
