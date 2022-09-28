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
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.AlertDialog
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Loader() =
    Box(
        contentAlignment = Center,
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.background).padding(20.dp)
    ) {
        CircularProgressIndicator()
    }

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ModalLoader(text: String? = null) = AlertDialog(
    modifier = Modifier.padding(top = 16.dp),
    onDismissRequest = {},
    buttons = {},
    text = {
        Row(
            horizontalArrangement = spacedBy(16.dp, alignment = CenterHorizontally),
            verticalAlignment = CenterVertically,
            modifier = Modifier.widthIn(min = 200.dp)
        ) {
            CircularProgressIndicator()
            text?.let { Text(text) }
        }
    },
)
