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

package org.briarproject.briar.desktop.forums

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.TextFieldExt.moveFocusOnTab
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIconDefaults
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import org.briarproject.bramble.api.sync.MessageId
import org.briarproject.briar.api.forum.ForumConstants.MAX_FORUM_POST_TEXT_LENGTH
import org.briarproject.briar.desktop.theme.sendButton
import org.briarproject.briar.desktop.ui.HorizontalDivider
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n

@Composable
@OptIn(ExperimentalComposeUiApi::class)
fun GroupInputComposable(
    selectedPost: State<MessageId?>,
    onSend: (String) -> Unit,
) {
    val postText = rememberSaveable { mutableStateOf("") }
    val onSendAction = {
        val text = postText.value
        if (text.isNotBlank() && postText.value.length <= MAX_FORUM_POST_TEXT_LENGTH) {
            onSend(text)
            postText.value = ""
        }
    }
    Column {
        HorizontalDivider()
        TextField(
            value = postText.value,
            onValueChange = { postText.value = it },
            onEnter = onSendAction,
            maxLines = 10,
            textStyle = MaterialTheme.typography.body1,
            placeholder = {
                Text(
                    text = if (selectedPost.value == null) {
                        i18n("forum.message.hint")
                    } else {
                        i18n("forum.message.reply.hint")
                    },
                    style = MaterialTheme.typography.body1,
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .moveFocusOnTab(),
            shape = RoundedCornerShape(0.dp),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = MaterialTheme.colors.background,
                focusedIndicatorColor = MaterialTheme.colors.background,
                unfocusedIndicatorColor = MaterialTheme.colors.background
            ),
            trailingIcon = {
                IconButton(
                    icon = Icons.Filled.Send,
                    iconTint = MaterialTheme.colors.sendButton,
                    contentDescription = i18n("access.message.send"),
                    onClick = onSendAction,
                    modifier = Modifier
                        .padding(4.dp)
                        .size(32.dp)
                        .pointerHoverIcon(PointerIconDefaults.Default),
                )
            }
        )
    }
}
