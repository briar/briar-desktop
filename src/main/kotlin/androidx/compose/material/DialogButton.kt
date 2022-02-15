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

package androidx.compose.material

import androidx.compose.runtime.Composable
import org.briarproject.briar.desktop.theme.buttonTextNegative
import org.briarproject.briar.desktop.theme.buttonTextPositive
import java.util.Locale

enum class ButtonType {
    NEUTRAL,
    DESTRUCTIVE,
}

@Composable
fun DialogButton(
    onClick: () -> Unit,
    text: String,
    type: ButtonType,
) {
    TextButton(onClick = onClick) {
        Text(
            text.uppercase(Locale.getDefault()),
            style = MaterialTheme.typography.button,
            color = when (type) {
                ButtonType.NEUTRAL -> MaterialTheme.colors.buttonTextPositive
                ButtonType.DESTRUCTIVE -> MaterialTheme.colors.buttonTextNegative
            },
        )
    }
}
