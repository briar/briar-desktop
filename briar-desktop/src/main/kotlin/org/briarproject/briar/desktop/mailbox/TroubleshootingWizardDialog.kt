/*
 * Briar Desktop
 * Copyright (C) 2023 The Briar Project
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

package org.briarproject.briar.desktop.mailbox

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.AlertDialog
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ButtonType
import androidx.compose.material.DialogButton
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import org.briarproject.briar.desktop.ui.Constants.DIALOG_WIDTH
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n
import org.briarproject.briar.desktop.utils.PreviewUtils

@Suppress("HardCodedStringLiteral")
fun main() = PreviewUtils.preview {
    val wizardDialogVisible = remember { mutableStateOf(true) }
    if (wizardDialogVisible.value) TroubleshootingWizardDialog(
        close = { wizardDialogVisible.value = false },
        onCheckConnection = { wizardDialogVisible.value = false },
        onUnlink = { wizardDialogVisible.value = false },
    )
}

@Composable
@OptIn(ExperimentalMaterialApi::class)
fun TroubleshootingWizardDialog(
    close: () -> Unit,
    onCheckConnection: () -> Unit,
    onUnlink: () -> Unit,
) {
    AlertDialog(
        modifier = Modifier.defaultMinSize(minWidth = DIALOG_WIDTH),
        onDismissRequest = close,
        title = {
            Text(
                text = i18n("mailbox.error.wizard.title"),
                modifier = Modifier.width(IntrinsicSize.Max),
                style = MaterialTheme.typography.h6,
            )
        },
        text = { Troubleshooting(close, onCheckConnection, onUnlink) },
        confirmButton = {
            DialogButton(
                onClick = { close() },
                text = i18n("back"),
                type = ButtonType.NEUTRAL,
            )
        },
    )
}

@Composable
@Suppress("LocalVariableName")
private fun Troubleshooting(
    close: () -> Unit,
    onCheckConnection: () -> Unit,
    onUnlink: () -> Unit,
) {
    Column(verticalArrangement = spacedBy(16.dp)) {
        val answer1Selected = rememberSaveable { mutableStateOf(false) }
        val answer2Selected = rememberSaveable { mutableStateOf(false) }
        val answer3Selected = rememberSaveable { mutableStateOf(false) }

        Text(i18n("mailbox_error.wizard.question1"))
        val radioOptions = listOf(
            i18n("mailbox_error.wizard.answer1"),
            i18n("mailbox_error.wizard.answer2"),
            i18n("mailbox_error.wizard.answer3"),
        )
        RadioGroup(radioOptions) { answer ->
            when (answer) {
                i18n("mailbox_error.wizard.answer1") -> {
                    answer1Selected.value = true
                    answer2Selected.value = false
                    answer3Selected.value = false
                }
                i18n("mailbox_error.wizard.answer2") -> {
                    answer1Selected.value = false
                    answer2Selected.value = true
                    answer3Selected.value = false
                }
                i18n("mailbox_error.wizard.answer3") -> {
                    answer1Selected.value = false
                    answer2Selected.value = false
                    answer3Selected.value = true
                }
            }
        }
        AnimatedVisibility(answer1Selected.value) {
            TroubleshootingAccess(close, onCheckConnection, onUnlink)
        }
        AnimatedVisibility(answer2Selected.value) {
            Text(i18n("mailbox_error.wizard.info2"))
        }
        AnimatedVisibility(answer3Selected.value) {
            UnlinkAnswer(i18n("mailbox_error.wizard.info3"), close, onUnlink)
        }
    }
}

@Composable
@Suppress("LocalVariableName")
private fun TroubleshootingAccess(close: () -> Unit, onCheckConnection: () -> Unit, onUnlink: () -> Unit) {
    Column(verticalArrangement = spacedBy(16.dp)) {
        val answer1_1Selected = rememberSaveable { mutableStateOf(false) }
        val answer1_2Selected = rememberSaveable { mutableStateOf(false) }
        val answer1_3Selected = rememberSaveable { mutableStateOf(false) }
        val answer1_4Selected = rememberSaveable { mutableStateOf(false) }

        Text(i18n("mailbox_error.wizard.info1_1"))
        Text(i18n("mailbox_error.wizard.question1_1"))

        val radioOptions1 = listOf(
            i18n("mailbox_error.wizard.answer1_1"),
            i18n("mailbox_error.wizard.answer1_2"),
            i18n("mailbox_error.wizard.answer1_3"),
            i18n("mailbox_error.wizard.answer1_4"),
        )
        RadioGroup(radioOptions1) { answer ->
            when (answer) {
                i18n("mailbox_error.wizard.answer1_1") -> {
                    answer1_1Selected.value = true
                    answer1_2Selected.value = false
                    answer1_3Selected.value = false
                    answer1_4Selected.value = false
                }
                i18n("mailbox_error.wizard.answer1_2") -> {
                    answer1_1Selected.value = false
                    answer1_2Selected.value = true
                    answer1_3Selected.value = false
                    answer1_4Selected.value = false
                }
                i18n("mailbox_error.wizard.answer1_3") -> {
                    answer1_1Selected.value = false
                    answer1_2Selected.value = false
                    answer1_3Selected.value = true
                    answer1_4Selected.value = false
                }
                i18n("mailbox_error.wizard.answer1_4") -> {
                    answer1_1Selected.value = false
                    answer1_2Selected.value = false
                    answer1_3Selected.value = false
                    answer1_4Selected.value = true
                }
            }
        }
        AnimatedVisibility(answer1_1Selected.value) {
            UnlinkAnswer(i18n("mailbox_error.wizard.info1_1_1"), close, onUnlink)
        }
        AnimatedVisibility(answer1_2Selected.value) {
            UnlinkAnswer(i18n("mailbox_error.wizard.info_1_1_2"), close, onUnlink)
        }
        AnimatedVisibility(answer1_3Selected.value) {
            Column(verticalArrangement = spacedBy(16.dp)) {
                Text(i18n("mailbox_error.wizard.info1_1_3"))
                OutlinedButton(
                    onClick = {
                        close()
                        onCheckConnection()
                    },
                    modifier = Modifier.align(CenterHorizontally),
                ) {
                    Text(
                        text = i18n("mailbox.status.check.connection.button"),
                    )
                }
            }
        }
        AnimatedVisibility(answer1_4Selected.value) {
            Text(i18n("mailbox_error.wizard.info1_1_4"))
        }
    }
}

@Composable
fun RadioGroup(radioOptions: List<String>, modifier: Modifier = Modifier, onSelected: (String) -> Unit) {
    val (selectedOption, onOptionSelected) = rememberSaveable { mutableStateOf("") }
    Column(modifier.selectableGroup()) {
        radioOptions.forEach { text ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(
                        selected = (text == selectedOption),
                        onClick = {
                            onOptionSelected(text)
                            onSelected(text)
                        },
                        role = Role.RadioButton,
                    )
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = CenterVertically,
            ) {
                RadioButton(
                    selected = (text == selectedOption),
                    onClick = null, // null recommended for accessibility with screen readers
                )
                Text(
                    text = text,
                    style = MaterialTheme.typography.body1.merge(),
                    modifier = Modifier.padding(start = 16.dp),
                )
            }
        }
    }
}

@Composable
private fun UnlinkAnswer(text: String, close: () -> Unit, onUnlink: () -> Unit) {
    Column(verticalArrangement = spacedBy(16.dp)) {
        Text(text)
        OutlinedButton(
            onClick = {
                close()
                onUnlink()
            },
            modifier = Modifier.align(CenterHorizontally),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colors.error),
        ) {
            Text(
                text = i18n("mailbox.status.unlink.button"),
            )
        }
    }
}
