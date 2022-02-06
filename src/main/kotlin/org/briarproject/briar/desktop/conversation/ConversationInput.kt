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

package org.briarproject.briar.desktop.conversation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.ResourceLoader
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.briarproject.briar.desktop.ui.HorizontalDivider
import org.briarproject.briar.desktop.ui.LocalWindowScope
import org.briarproject.briar.desktop.utils.ImagePicker.pickImageUsingDialog
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n
import org.briarproject.briar.desktop.utils.PreviewUtils.preview

@Suppress("HardCodedStringLiteral")
@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
fun main() = preview {
    val bitmap = ResourceLoader.Default.load("images/logo_circle.png").use {
        loadImageBitmap(it)
    }
    val (text, updateText) = remember { mutableStateOf("Lorem ipsum.") }
    val (image, updateImage) = remember { mutableStateOf<ImageBitmap?>(bitmap) }
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
            textStyle = TextStyle(fontSize = 16.sp, lineHeight = 16.sp),
            placeholder = { Text(i18n("conversation.message.new")) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(0.dp),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = MaterialTheme.colors.background,
                focusedIndicatorColor = MaterialTheme.colors.background,
                unfocusedIndicatorColor = MaterialTheme.colors.background
            ),
            leadingIcon = {
                val windowScope = LocalWindowScope.current!!
                IconButton(
                    onClick = {
                        if (image == null) {
                            pickImageUsingDialog(windowScope.window, updateImage)
                        } else {
                            updateImage(null)
                        }
                    },
                    Modifier.padding(4.dp).size(32.dp)
                        .background(MaterialTheme.colors.primary, CircleShape),
                ) {
                    if (image == null) {
                        Icon(Icons.Filled.Add, i18n("access.attachment_add"), Modifier.size(24.dp), Color.White)
                    } else {
                        Icon(Icons.Filled.Close, i18n("access.attachment_remove"), Modifier.size(24.dp), Color.White)
                    }
                }
            },
            trailingIcon = {
                IconButton(
                    onClick = onSend, modifier = Modifier.padding(4.dp).size(32.dp),
                ) {
                    Icon(
                        Icons.Filled.Send,
                        i18n("access.message.send"),
                        tint = MaterialTheme.colors.secondary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        )
    }
}
