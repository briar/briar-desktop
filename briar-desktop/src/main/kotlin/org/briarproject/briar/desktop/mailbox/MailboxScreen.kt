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

import androidx.compose.runtime.Composable
import org.briarproject.bramble.api.mailbox.MailboxPairingState.ConnectionError
import org.briarproject.bramble.api.mailbox.MailboxPairingState.InvalidQrCode
import org.briarproject.bramble.api.mailbox.MailboxPairingState.MailboxAlreadyPaired
import org.briarproject.bramble.api.mailbox.MailboxPairingState.Paired
import org.briarproject.bramble.api.mailbox.MailboxPairingState.Pending
import org.briarproject.bramble.api.mailbox.MailboxPairingState.UnexpectedError
import org.briarproject.briar.desktop.mailbox.MailboxPairingUiState.IsPaired
import org.briarproject.briar.desktop.mailbox.MailboxPairingUiState.NotSetup
import org.briarproject.briar.desktop.mailbox.MailboxPairingUiState.OfflineWhenPairing
import org.briarproject.briar.desktop.mailbox.MailboxPairingUiState.Pairing
import org.briarproject.briar.desktop.mailbox.MailboxPairingUiState.Unknown
import org.briarproject.briar.desktop.mailbox.MailboxPairingUiState.WasUnpaired
import org.briarproject.briar.desktop.ui.Loader
import org.briarproject.briar.desktop.ui.UiPlaceholder
import org.briarproject.briar.desktop.viewmodel.viewModel

@Composable
fun MailboxScreen(viewModel: MailboxViewModel = viewModel()) {
    when (val state = viewModel.pairingState.value) {
        Unknown -> Loader()
        NotSetup -> MailboxSetupScreen(viewModel, false)
        is Pairing -> when (state.pairingState) {
            is Pending -> MailboxSetupScreen(viewModel, false)
            is InvalidQrCode, is MailboxAlreadyPaired, is ConnectionError, is UnexpectedError -> {
                MailboxSetupScreen(viewModel, true)
            }
            is Paired -> MailboxStatusScreen(
                status = viewModel.status.value,
                isCheckingConnection = false, // we just paired, there was no time to trigger a connection check
                onCheckConnection = viewModel::checkConnection,
            )
        }
        OfflineWhenPairing -> MailboxSetupScreen(viewModel, true)
        is IsPaired -> MailboxStatusScreen(
            status = viewModel.status.value,
            isCheckingConnection = state.connectionCheckRunning,
            onCheckConnection = viewModel::checkConnection,
        )
        is WasUnpaired -> UiPlaceholder() // TODO
    }
}
