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

package org.briarproject.briar.desktop.mailbox

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.briarproject.bramble.api.mailbox.MailboxPairingState
import org.briarproject.briar.desktop.mailbox.MailboxPairingUiState.OfflineWhenPairing
import org.briarproject.briar.desktop.mailbox.MailboxPairingUiState.Pairing
import org.briarproject.briar.desktop.mailbox.MailboxPairingUiState.WasUnpaired
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n
import org.briarproject.briar.desktop.utils.PreviewUtils.preview

@Suppress("HardCodedStringLiteral")
fun main() = preview("visible" to true) {
    val visible = getBooleanParameter("visible")
    MailboxErrorDialog(OfflineWhenPairing, visible) { setBooleanParameter("visible", false) }
}

@Composable
@OptIn(ExperimentalMaterialApi::class)
fun MailboxErrorDialog(
    state: MailboxPairingUiState,
    visible: Boolean,
    onDismissed: () -> Unit,
) {
    if (!visible) return
    AlertDialog(
        title = {
            if (state is WasUnpaired) Text(i18n("mailbox.setup.intro.title"))
            else Text(i18n("mailbox.setup.error.title"))
        },
        onDismissRequest = onDismissed,
        text = {
            // Add empty box here with a minimum size to prevent overly narrow dialog
            Box(modifier = Modifier.defaultMinSize(300.dp))
            Text(text = state.getError())
        },
        confirmButton = {
            Button(
                onClick = onDismissed,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(i18n("ok"))
            }
        },
    )
}

private fun MailboxPairingUiState.getError(): String = when (this) {
    is OfflineWhenPairing -> i18n("mailbox.setup.offline_error_title") + "\n\n" +
        i18n("mailbox.setup.offline_error_description")
    is Pairing -> when (val s = pairingState) {
        is MailboxPairingState.InvalidQrCode -> i18n("mailbox.setup.link.error")
        is MailboxPairingState.MailboxAlreadyPaired -> i18n("mailbox.setup.already_paired_title") +
            "\n\n" + i18n("mailbox.setup.already_paired_description")
        is MailboxPairingState.ConnectionError -> i18n("mailbox.setup.io_error_title") +
            "\n\n" + i18n("mailbox.setup.io_error_description")
        is MailboxPairingState.UnexpectedError -> i18n("mailbox.setup.assertion_error_description")
        else -> error("Unhandled pairing state: ${s::class.simpleName}")
    }
    is WasUnpaired -> if (tellUserToWipeMailbox) i18n("mailbox.unlink.no_wipe.title") +
        "\n\n" + i18n("mailbox.unlink.no_wipe.message") else i18n("mailbox.unlink.no_wipe.title")
    else -> error("Unhandled state: ${this::class.simpleName}")
}
