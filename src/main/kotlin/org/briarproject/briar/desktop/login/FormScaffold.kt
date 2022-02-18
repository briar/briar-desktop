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

package org.briarproject.briar.desktop.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.requiredWidthIn
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.briarproject.briar.desktop.ui.Constants.STARTUP_FIELDS_WIDTH
import org.briarproject.briar.desktop.utils.InternationalizationUtils

@Composable
fun FormScaffold(
    explanationText: String?,
    buttonText: String,
    buttonClick: () -> Unit,
    buttonEnabled: Boolean,
    content: @Composable () -> Unit,
) = Column(
    modifier = Modifier.requiredWidthIn(max = STARTUP_FIELDS_WIDTH),
    horizontalAlignment = Alignment.CenterHorizontally
) {
    if (explanationText != null) {
        Spacer(Modifier.weight(0.5f))
        Text(
            explanationText,
            style = MaterialTheme.typography.body2,
            modifier = Modifier.requiredWidth(STARTUP_FIELDS_WIDTH)
        )
        Spacer(Modifier.weight(0.5f))
    } else Spacer(Modifier.weight(1.0f))
    content()
    Spacer(Modifier.weight(1.0f))
    Button(onClick = buttonClick, enabled = buttonEnabled, modifier = Modifier.fillMaxWidth()) {
        Text(buttonText.uppercase(InternationalizationUtils.locale))
    }
}
