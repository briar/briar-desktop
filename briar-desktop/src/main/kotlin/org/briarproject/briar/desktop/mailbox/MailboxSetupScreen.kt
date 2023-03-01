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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.End
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import org.briarproject.bramble.api.FormatException
import org.briarproject.briar.desktop.mailbox.MailboxPairingUiState.NotSetup
import org.briarproject.briar.desktop.ui.Constants.DIALOG_WIDTH
import org.briarproject.briar.desktop.ui.HorizontalDivider
import org.briarproject.briar.desktop.utils.AccessibilityUtils.description
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n

@Composable
fun MailboxSetupScreen(viewModel: MailboxViewModel, showError: Boolean) {
    MailboxErrorDialog(
        state = viewModel.pairingUiState.value,
        visible = showError,
    ) {
        viewModel.onPairingErrorSeen()
    }
    Box {
        val scrollState = rememberScrollState()
        VerticalScrollbar(
            adapter = rememberScrollbarAdapter(scrollState),
            modifier = Modifier.align(CenterEnd).fillMaxHeight()
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = CenterHorizontally,
            modifier = Modifier.verticalScroll(scrollState).padding(16.dp).fillMaxSize(),
        ) {
            val theme = if (MaterialTheme.colors.isLight) "light" else "dark" // NON-NLS
            Image(
                painter = painterResource("images/il_mailbox_$theme.svg"), // NON-NLS
                contentDescription = i18n("access.logo"),
            )
            Text(
                text = i18n("mailbox.setup.intro"),
                modifier = Modifier.widthIn(max = DIALOG_WIDTH),
            )
            Image(
                painter = painterResource("images/il_mailbox_setup_$theme.svg"), // NON-NLS
                contentDescription = i18n("access.logo"),
            )
            HorizontalDivider(Modifier.widthIn(max = DIALOG_WIDTH * 2))
            Text(
                text = i18n("mailbox.setup.download"),
                modifier = Modifier.widthIn(max = DIALOG_WIDTH).padding(top = 16.dp),
            )
            Text(
                text = i18n("mailbox.setup.link"),
                modifier = Modifier.widthIn(max = DIALOG_WIDTH),
            )

            val isInvalid = rememberSaveable { mutableStateOf(false) }
            val onNameChanged = { changedName: String ->
                viewModel.onPairingLinkChanged(changedName)
                isInvalid.value = false
            }
            val onOkButtonClicked = {
                try {
                    viewModel.pairMailbox(viewModel.pairingLink.value)
                } catch (e: FormatException) {
                    isInvalid.value = true
                }
            }
            Column(Modifier.widthIn(max = DIALOG_WIDTH * 2)) {
                OutlinedTextField(
                    value = viewModel.pairingLink.value,
                    onValueChange = onNameChanged,
                    label = { Text(i18n("mailbox.setup.hint")) },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    onEnter = onOkButtonClicked,
                    isError = isInvalid.value,
                    errorMessage = i18n("mailbox.setup.link.error"),
                    modifier = Modifier
                        .fillMaxWidth()
                        .description(i18n("mailbox.setup.hint")),
                )
                if (viewModel.pairingUiState.value is NotSetup) Button(
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
