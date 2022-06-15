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

package org.briarproject.briar.desktop.contact

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.width
import androidx.compose.material.AlertDialog
import androidx.compose.material.ButtonType
import androidx.compose.material.DialogButton
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n
import org.briarproject.briar.desktop.utils.PreviewUtils.preview

@Suppress("HardCodedStringLiteral")
fun main() = preview(
    "visible" to true,
) {
    ConfirmRemovePendingContactDialog(
        getBooleanParameter("visible"),
        { setBooleanParameter("visible", false) },
        { setBooleanParameter("visible", false) },
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ConfirmRemovePendingContactDialog(
    isVisible: Boolean,
    close: () -> Unit,
    onRemove: () -> Unit,
) {
    if (!isVisible) return

    AlertDialog(
        onDismissRequest = close,
        title = {
            Text(
                text = i18n("contacts.pending.remove.dialog.title"),
                modifier = Modifier.width(IntrinsicSize.Max),
                style = MaterialTheme.typography.h6,
            )
        },
        text = {
            // Add empty box here with a minimum size to prevent overly narrow dialog
            Box(modifier = Modifier.defaultMinSize(300.dp))
            Text(i18n("contacts.pending.remove.dialog.message"))
        },
        dismissButton = {
            DialogButton(
                onClick = close,
                text = i18n("cancel"),
                type = ButtonType.NEUTRAL
            )
        },
        confirmButton = {
            DialogButton(
                onClick = onRemove,
                text = i18n("remove"),
                type = ButtonType.DESTRUCTIVE
            )
        },
    )
}
