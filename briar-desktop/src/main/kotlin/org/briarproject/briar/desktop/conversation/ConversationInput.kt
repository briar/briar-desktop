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

package org.briarproject.briar.desktop.conversation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TextFieldExt.moveFocusOnTab
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import org.briarproject.briar.desktop.theme.sendButton
import org.briarproject.briar.desktop.ui.ColoredIconButton
import org.briarproject.briar.desktop.ui.HorizontalDivider
import org.briarproject.briar.desktop.ui.LocalWindowScope
import org.briarproject.briar.desktop.utils.ImagePicker.pickImageUsingDialog
import org.briarproject.briar.desktop.utils.ImageUtils.loadImageBitmap
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n
import org.briarproject.briar.desktop.utils.PreviewUtils.preview
import org.briarproject.briar.desktop.utils.ResourceUtils.getResourceAsStream

@Suppress("HardCodedStringLiteral")
fun main() = preview {
    val bitmap = getResourceAsStream("/images/logo_circle.png")?.use {
        loadImageBitmap(it)
    }
    val (text, updateText) = remember { mutableStateOf("Lorem ipsum.") }
    val (image, updateImage) = remember { mutableStateOf(bitmap) }
    var dialogVisible by remember { mutableStateOf(false) }
    var sentText by remember { mutableStateOf("") }
    ConversationInput(text, updateText, image, updateImage) {
        dialogVisible = true; sentText = text; updateText(""); updateImage(null)
    }

    if (dialogVisible) {
        AlertDialog(
            onDismissRequest = { dialogVisible = false },
            buttons = {},
            text = { Text(sentText) },
        )
    }
}

@Composable
fun ConversationInput(
    text: String,
    updateText: (String) -> Unit,
    image: ImageBitmap?,
    updateImage: (ImageBitmap?) -> Unit,
    onSend: () -> Unit,
) {
    Column {
        HorizontalDivider()
        if (image != null) {
            Image(image, null, modifier = Modifier.heightIn(100.dp, 200.dp))
        }
        TextField(
            value = text,
            onValueChange = updateText,
            onEnter = onSend,
            maxLines = 10,
            textStyle = MaterialTheme.typography.body1,
            placeholder = { Text(i18n("conversation.message.new"), style = MaterialTheme.typography.body1) },
            modifier = Modifier
                .fillMaxWidth()
                .moveFocusOnTab(),
            shape = RoundedCornerShape(0.dp),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = MaterialTheme.colors.background,
                focusedIndicatorColor = MaterialTheme.colors.background,
                unfocusedIndicatorColor = MaterialTheme.colors.background
            ),
            leadingIcon = {
                val windowScope = LocalWindowScope.current!!
                ColoredIconButton(
                    icon = if (image == null) Icons.Filled.Add else Icons.Filled.Close,
                    contentDescription = if (image == null) i18n("access.attachment_add") else i18n("access.attachment_remove"),
                    onClick = {
                        if (image == null) {
                            pickImageUsingDialog(windowScope.window, updateImage)
                        } else {
                            updateImage(null)
                        }
                    },
                    modifier = Modifier.padding(4.dp),
                )
            },
            trailingIcon = {
                IconButton(
                    icon = Icons.AutoMirrored.Filled.Send,
                    iconTint = MaterialTheme.colors.sendButton,
                    contentDescription = i18n("access.message.send"),
                    onClick = onSend,
                    modifier = Modifier.padding(4.dp).size(32.dp).pointerHoverIcon(PointerIcon.Default),
                )
            }
        )
    }
}
