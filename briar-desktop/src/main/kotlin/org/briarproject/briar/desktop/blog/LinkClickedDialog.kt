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

package org.briarproject.briar.desktop.blog

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipPlacement
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.ButtonType
import androidx.compose.material.DialogButton
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import org.briarproject.briar.desktop.theme.surfaceVariant
import org.briarproject.briar.desktop.ui.Tooltip
import org.briarproject.briar.desktop.utils.DesktopUtils.browseLinkIfSupported
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n
import org.briarproject.briar.desktop.utils.PreviewUtils.preview

@Suppress("HardCodedStringLiteral")
fun main() = preview("visible" to true) {
    val visible = getBooleanParameter("visible")
    val link = "https://google.com"
    LinkClickedDialog(
        link, visible,
        onDismissed = {
            setBooleanParameter("visible", false)
        },
        onConfirmed = {
            setBooleanParameter("visible", false)
            browseLinkIfSupported(link)
        }
    )
}

@Composable
@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
fun LinkClickedDialog(
    link: String,
    visible: Boolean,
    onDismissed: () -> Unit,
    onConfirmed: () -> Unit,
) {
    if (!visible) return
    val clipboardManager = LocalClipboardManager.current
    AlertDialog(
        title = {
            Text(i18n("link.warning.title"), style = MaterialTheme.typography.h6)
        },
        onDismissRequest = onDismissed,
        text = {
            // Add empty box here with a minimum size to prevent overly narrow dialog
            Box(modifier = Modifier.defaultMinSize(300.dp))
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(i18n("link.warning.intro"))
                Box(
                    modifier = Modifier.background(MaterialTheme.colors.surfaceVariant, RoundedCornerShape(4.dp))
                        .fillMaxWidth().clickable {
                            clipboardManager.setText(AnnotatedString(link))
                        }
                ) {
                    Text(
                        text = link,
                        style = TextStyle(fontFamily = FontFamily.Monospace),
                        modifier = Modifier.padding(16.dp)
                    )
                    Tooltip(
                        text = i18n("copy"),
                        modifier = Modifier.align(Alignment.CenterEnd),
                        delayMillis = 200,
                        tooltipPlacement = TooltipPlacement.ComponentRect(
                            alignment = Alignment.BottomCenter,
                        )
                    ) {
                        Icon(
                            Icons.Filled.ContentCopy,
                            "copy",
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
                Text(i18n("link.warning.text"))
            }
        },
        dismissButton = {
            DialogButton(
                onClick = onDismissed,
                text = i18n("cancel"),
                type = ButtonType.NEUTRAL,
            )
        },
        confirmButton = {
            DialogButton(
                onClick = onConfirmed,
                text = i18n("link.warning.open.link"),
                type = ButtonType.DESTRUCTIVE,
            )
        },
    )
}
