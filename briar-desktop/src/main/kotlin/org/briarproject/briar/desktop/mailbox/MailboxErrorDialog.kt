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

package org.briarproject.briar.desktop.mailbox

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberDialogState
import org.briarproject.bramble.api.mailbox.MailboxPairingState
import org.briarproject.briar.desktop.mailbox.MailboxPairingUiState.OfflineWhenPairing
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n
import org.briarproject.briar.desktop.utils.PreviewUtils.preview
import java.awt.Dimension

fun main() = preview {
    val visible = mutableStateOf(true)
    MailboxErrorDialog(OfflineWhenPairing, visible.value) { visible.value = false }
}

@Composable
fun MailboxErrorDialog(
    state: MailboxPairingUiState,
    visible: Boolean,
    onDismissed: () -> Unit,
) {
    if (!visible) return
    Dialog(
        title = i18n("mailbox.setup.error.title"),
        onCloseRequest = onDismissed,
        state = rememberDialogState(
            position = WindowPosition(Alignment.Center),
        ),
    ) {
        window.minimumSize = Dimension(360, 180)
        val scaffoldState = rememberScaffoldState()
        Surface {
            Scaffold(
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .padding(top = 24.dp, bottom = 12.dp),
                scaffoldState = scaffoldState,
                content = {
                    Text(
                        text = state.getError(),
                    )
                },
                bottomBar = {
                    Button(
                        onClick = onDismissed,
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text(i18n("ok"))
                    }
                },
            )
        }
    }
}

private fun MailboxPairingUiState.getError(): String = when (this) {
    is OfflineWhenPairing -> i18n("mailbox.setup.offline_error_title") + "\n\n" +
        i18n("mailbox.setup.offline_error_description")
    is MailboxPairingUiState.Pairing -> when (val s = pairingState) {
        is MailboxPairingState.InvalidQrCode -> i18n("mailbox.setup.link.error")
        is MailboxPairingState.MailboxAlreadyPaired -> i18n("mailbox.setup.already_paired_title") +
            "\n\n" + i18n("mailbox.setup.already_paired_description")
        is MailboxPairingState.ConnectionError -> i18n("mailbox.setup.io_error_title") +
            "\n\n" + i18n("mailbox.setup.io_error_description")
        is MailboxPairingState.UnexpectedError -> i18n("mailbox.setup.assertion_error_description")
        else -> error("Unhandled pairing state: ${s::class.simpleName}")
    }
    else -> error("Unhandled state: ${this::class.simpleName}")
}
