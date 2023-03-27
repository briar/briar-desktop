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

import androidx.compose.foundation.Image
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.End
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import org.briarproject.bramble.api.FormatException
import org.briarproject.bramble.api.mailbox.MailboxPairingState
import org.briarproject.briar.desktop.mailbox.MailboxPairingUiState.NotSetup
import org.briarproject.briar.desktop.ui.Constants.DIALOG_WIDTH
import org.briarproject.briar.desktop.utils.AccessibilityUtils.description
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n
import org.briarproject.briar.desktop.utils.PreviewUtils

fun main() = PreviewUtils.preview(
    "showError" to false,
) {
    val pairingLink = remember { mutableStateOf("") }
    val isSettingUp = remember { mutableStateOf(false) }
    val showError = getBooleanParameter("showError")
    MailboxSetupScreen(
        pairingUiState = if (showError) {
            MailboxPairingUiState.OfflineWhenPairing
        } else if (isSettingUp.value) {
            MailboxPairingUiState.Pairing(MailboxPairingState.Pairing(System.currentTimeMillis()))
        } else {
            NotSetup
        },
        pairingLink = pairingLink.value,
        onPairingLinkChanged = { pairingLink.value = it },
        pairMailbox = { isSettingUp.value = true },
        showError = showError,
        onPairingErrorSeen = { setBooleanParameter("showError", false) },
    )
}

@Composable
fun MailboxSetupScreen(viewModel: MailboxViewModel, showError: Boolean) {
    MailboxSetupScreen(
        pairingUiState = viewModel.pairingUiState.value,
        pairingLink = viewModel.pairingLink.value,
        onPairingLinkChanged = viewModel::onPairingLinkChanged,
        pairMailbox = viewModel::pairMailbox,
        showError = showError,
        onPairingErrorSeen = viewModel::onPairingErrorSeen,
    )
}

@Composable
fun MailboxSetupScreen(
    pairingUiState: MailboxPairingUiState,
    pairingLink: String,
    onPairingLinkChanged: (String) -> Unit,
    pairMailbox: () -> Unit,
    showError: Boolean,
    onPairingErrorSeen: () -> Unit,
) {
    MailboxErrorDialog(
        state = pairingUiState,
        visible = showError,
        onDismissed = onPairingErrorSeen,
    )
    Box(
        contentAlignment = Center,
        modifier = Modifier.fillMaxSize(),
    ) {
        val scrollState = rememberScrollState()
        VerticalScrollbar(
            adapter = rememberScrollbarAdapter(scrollState),
            modifier = Modifier.align(CenterEnd).fillMaxHeight(),
        )
        Row(
            horizontalArrangement = spacedBy(32.dp, CenterHorizontally),
            modifier = Modifier.fillMaxWidth().verticalScroll(scrollState).padding(16.dp),
        ) {
            val theme = if (MaterialTheme.colors.isLight) "light" else "dark" // NON-NLS
            Column(
                verticalArrangement = spacedBy(16.dp),
                modifier = Modifier.weight(0.5f, fill = false).widthIn(max = DIALOG_WIDTH),
            ) {
                Image(
                    painter = painterResource("images/il_mailbox_$theme.svg"), // NON-NLS
                    contentDescription = null,
                    modifier = Modifier.align(CenterHorizontally),
                )
                Text(
                    text = i18n("mailbox.setup.intro.title"),
                    style = MaterialTheme.typography.h5,
                )
                Text(
                    text = i18n("mailbox.setup.intro"),
                )
                Text(
                    text = i18n("mailbox.setup.download.title"),
                    style = MaterialTheme.typography.h5,
                )
                Text(
                    text = i18n("mailbox.setup.download"),
                )
            }
            Column(
                verticalArrangement = spacedBy(16.dp),
                modifier = Modifier.weight(0.5f, fill = false).widthIn(max = DIALOG_WIDTH),
            ) {
                Image(
                    painter = painterResource("images/il_mailbox_setup_$theme.svg"), // NON-NLS
                    contentDescription = null,
                    modifier = Modifier.align(CenterHorizontally),
                )
                Text(
                    text = i18n("mailbox.setup.link.title"),
                    style = MaterialTheme.typography.h5,
                )
                Text(
                    text = i18n("mailbox.setup.link"),
                )
                val isInvalid = rememberSaveable { mutableStateOf(false) }
                val onNameChanged = { changedName: String ->
                    onPairingLinkChanged(changedName)
                    isInvalid.value = false
                }
                val onOkButtonClicked = {
                    try {
                        pairMailbox()
                    } catch (e: FormatException) {
                        isInvalid.value = true
                    }
                }
                OutlinedTextField(
                    value = pairingLink,
                    onValueChange = onNameChanged,
                    label = { Text(i18n("mailbox.setup.hint")) },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    onEnter = onOkButtonClicked,
                    isError = isInvalid.value,
                    errorMessage = i18n("mailbox.setup.link.error"),
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 96.dp)
                        .description(i18n("mailbox.setup.hint")),
                )
                if (pairingUiState is NotSetup) Button(
                    onClick = onOkButtonClicked,
                    modifier = Modifier.align(End)
                ) {
                    Text(i18n("mailbox.setup.button"))
                } else {
                    CircularProgressIndicator(modifier = Modifier.align(End))
                }
            }
        }
    }
}
