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

package org.briarproject.briar.desktop.threadedgroup.conversation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment.Companion.Top
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import org.briarproject.bramble.util.StringUtils.utf8IsTooLong
import org.briarproject.briar.desktop.theme.divider
import org.briarproject.briar.desktop.theme.sendButton
import org.briarproject.briar.desktop.threadedgroup.ThreadedGroupStrings
import org.briarproject.briar.desktop.ui.HorizontalDivider
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n
import org.briarproject.briar.desktop.utils.StringUtils.takeUtf8

@Composable
@OptIn(ExperimentalComposeUiApi::class)
fun ThreadedGroupConversationInput(
    strings: ThreadedGroupStrings,
    selectedThreadItem: ThreadItem?,
    onReplyClosed: () -> Unit,
    onSend: (String) -> Unit,
) {
    val messageText = rememberSaveable { mutableStateOf("") }
    val onSendAction = {
        val text = messageText.value
        if (text.isNotBlank() && !utf8IsTooLong(messageText.value, strings.messageMaxLength)) {
            onSend(text)
            messageText.value = ""
        }
    }
    Column {
        if (selectedThreadItem != null) {
            Row(
                verticalAlignment = Top,
                modifier = Modifier.border(1.dp, MaterialTheme.colors.divider),
            ) {
                Column(
                    modifier = Modifier
                        .padding(start = 8.dp, top = 8.dp, bottom = 8.dp)
                        .weight(1f),
                ) {
                    Text(
                        text = strings.messageReplyIntro,
                        modifier = Modifier.padding(bottom = 8.dp),
                    )
                    ThreadItemContent(
                        item = selectedThreadItem,
                        isPreview = true,
                        modifier = Modifier
                            .border(1.dp, MaterialTheme.colors.divider)
                            .background(
                                MaterialTheme.colors.onSurface.copy(
                                    alpha = TextFieldDefaults.BackgroundOpacity
                                )
                            ),
                    )
                }
                IconButton(
                    icon = Icons.Filled.Close,
                    contentDescription = strings.messageReplyClose,
                    onClick = onReplyClosed,
                )
            }
        }
        HorizontalDivider()
        TextField(
            value = messageText.value,
            onValueChange = { messageText.value = it.takeUtf8(strings.messageMaxLength) },
            onEnter = onSendAction,
            maxLines = 10,
            textStyle = MaterialTheme.typography.body1,
            placeholder = {
                Text(
                    text = if (selectedThreadItem == null) {
                        strings.messageHint
                    } else {
                        strings.messageReplyHint
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
                        .pointerHoverIcon(PointerIcon.Default),
                )
            }
        )
    }
}
