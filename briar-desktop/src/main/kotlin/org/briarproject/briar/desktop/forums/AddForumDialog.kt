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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberDialogState
import org.briarproject.briar.api.forum.ForumConstants.MAX_FORUM_NAME_LENGTH
import org.briarproject.briar.desktop.utils.AccessibilityUtils.description
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n
import org.briarproject.briar.desktop.utils.PreviewUtils
import java.awt.Dimension

fun main() = PreviewUtils.preview {
    val visible = mutableStateOf(true)
    AddForumDialog(visible, {}, { visible.value = false })
}

@Composable
fun AddForumDialog(
    visible: State<Boolean>,
    onCreate: (String) -> Unit,
    onCancelButtonClicked: () -> Unit,
) {
    Dialog(
        title = i18n("forum.add.title"),
        onCloseRequest = onCancelButtonClicked,
        state = rememberDialogState(
            position = WindowPosition(Alignment.Center),
        ),
        visible = visible.value,
    ) {
        window.minimumSize = Dimension(360, 180)
        val scaffoldState = rememberScaffoldState()
        val name = rememberSaveable { mutableStateOf("") }
        Surface {
            Scaffold(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .padding(top = 24.dp, bottom = 12.dp),
                scaffoldState = scaffoldState,
                topBar = {
                    Box(Modifier.fillMaxWidth()) {
                        Text(
                            text = i18n("forum.add.title"),
                            style = MaterialTheme.typography.h6,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }
                },
                content = {
                    AddForumContent(name, onCreate)
                },
                bottomBar = {
                    OkCancelBottomBar(
                        okButtonLabel = i18n("forum.add.button"),
                        okButtonEnabled = isValidForumName(name.value),
                        onOkButtonClicked = {
                            onCreate(name.value)
                            name.value = ""
                        },
                        onCancelButtonClicked = onCancelButtonClicked,
                    )
                },
            )
        }
    }
}

private fun isValidForumName(name: String): Boolean {
    return name.isNotBlank() && name.length <= MAX_FORUM_NAME_LENGTH
}

@Composable
fun AddForumContent(name: MutableState<String>, onCreate: (String) -> Unit) {
    val focusRequester = remember { FocusRequester() }
    OutlinedTextField(
        value = name.value,
        onValueChange = { if (it.length <= MAX_FORUM_NAME_LENGTH) name.value = it },
        label = { Text(i18n("forum.add.hint")) },
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        singleLine = true,
        onEnter = {
            onCreate(name.value)
            name.value = ""
        },
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester)
            .description(i18n("forum.add.hint")),
    )
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

@Composable
fun OkCancelBottomBar(
    okButtonLabel: String,
    okButtonEnabled: Boolean = true,
    onOkButtonClicked: () -> Unit,
    onCancelButtonClicked: () -> Unit,
) {
    Box(Modifier.fillMaxWidth()) {
        Row(Modifier.align(CenterEnd)) {
            TextButton(
                onClick = onCancelButtonClicked,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colors.error)
            ) {
                Text(i18n("cancel"))
            }
            Button(
                onClick = onOkButtonClicked,
                enabled = okButtonEnabled,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(okButtonLabel)
            }
        }
    }
}