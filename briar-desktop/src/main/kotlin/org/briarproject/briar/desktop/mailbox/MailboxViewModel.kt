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

import androidx.compose.runtime.mutableStateOf
import mu.KotlinLogging
import org.briarproject.bramble.api.Consumer
import org.briarproject.bramble.api.db.TransactionManager
import org.briarproject.bramble.api.event.Event
import org.briarproject.bramble.api.event.EventBus
import org.briarproject.bramble.api.lifecycle.LifecycleManager
import org.briarproject.bramble.api.mailbox.MailboxManager
import org.briarproject.bramble.api.mailbox.MailboxPairingState
import org.briarproject.bramble.api.mailbox.MailboxPairingState.Paired
import org.briarproject.bramble.api.mailbox.MailboxPairingTask
import org.briarproject.bramble.api.mailbox.MailboxStatus
import org.briarproject.bramble.api.mailbox.event.OwnMailboxConnectionStatusEvent
import org.briarproject.bramble.api.plugin.Plugin
import org.briarproject.bramble.api.plugin.PluginManager
import org.briarproject.bramble.api.plugin.TorConstants
import org.briarproject.bramble.api.plugin.event.TransportInactiveEvent
import org.briarproject.briar.desktop.mailbox.MailboxPairingUiState.IsPaired
import org.briarproject.briar.desktop.mailbox.MailboxPairingUiState.NotSetup
import org.briarproject.briar.desktop.mailbox.MailboxPairingUiState.OfflineWhenPairing
import org.briarproject.briar.desktop.mailbox.MailboxPairingUiState.Pairing
import org.briarproject.briar.desktop.mailbox.MailboxPairingUiState.Unknown
import org.briarproject.briar.desktop.threading.BriarExecutors
import org.briarproject.briar.desktop.threading.UiExecutor
import org.briarproject.briar.desktop.viewmodel.EventListenerDbViewModel
import org.briarproject.briar.desktop.viewmodel.asState
import javax.inject.Inject

sealed class MailboxPairingUiState {
    object Unknown : MailboxPairingUiState()
    object NotSetup : MailboxPairingUiState()
    class Pairing(val pairingState: MailboxPairingState) : MailboxPairingUiState()
    object OfflineWhenPairing : MailboxPairingUiState()
    class IsPaired(val isOnline: Boolean) : MailboxPairingUiState()
    class WasUnpaired(val tellUserToWipeMailbox: Boolean) : MailboxPairingUiState()
}

class MailboxViewModel @Inject constructor(
    lifecycleManager: LifecycleManager,
    db: TransactionManager,
    eventBus: EventBus,
    private val mailboxManager: MailboxManager,
    private val pluginManager: PluginManager,
    private val briarExecutors: BriarExecutors,
) : EventListenerDbViewModel(briarExecutors, lifecycleManager, db, eventBus),
    Consumer<MailboxPairingState> {

    companion object {
        private val LOG = KotlinLogging.logger {}
    }

    private val _pairingState = mutableStateOf<MailboxPairingUiState>(Unknown)
    val pairingState = _pairingState.asState()
    private val _pairingLink = mutableStateOf("")
    val pairingLink = _pairingLink.asState()
    private val _status = mutableStateOf<MailboxStatus?>(null)
    val status = _status.asState()

    @UiExecutor
    private var pairingTask: MailboxPairingTask? = null

    init {
        checkIfSetup()
    }

    @UiExecutor
    private fun checkIfSetup() {
        val task = mailboxManager.currentPairingTask
        if (task == null) briarExecutors.onDbThreadWithTransaction(true) { txn ->
            val isPaired = mailboxManager.isPaired(txn)
            if (isPaired) {
                val mailboxStatus = mailboxManager.getMailboxStatus(txn)
                val isOnline = isTorActive()
                briarExecutors.onUiThread {
                    _pairingState.value = IsPaired(isOnline)
                    _status.value = mailboxStatus
                }
            } else briarExecutors.onUiThread {
                _pairingState.value = NotSetup
            }
        } else {
            task.addObserver(this)
            pairingTask = task
        }
    }

    override fun eventOccurred(e: Event) {
        if (e is OwnMailboxConnectionStatusEvent) {
            _status.value = e.status
        } else if (e is TransportInactiveEvent) {
            if (TorConstants.ID != e.transportId) return
            onTorInactive()
        }
    }

    @UiExecutor
    private fun onTorInactive() {
        val lastState = _pairingState.value
        if (lastState is IsPaired) {
            // we are already paired, so use IsPaired state
            _pairingState.value = IsPaired(false)
        } else if (lastState is Pairing) {
            // check that we not just finished pairing (showing success screen)
            if (lastState.pairingState !is Paired) _pairingState.value = OfflineWhenPairing
            // else ignore offline event as user will be leaving UI flow anyway
        }
    }

    @UiExecutor
    override fun accept(t: MailboxPairingState) {
        @Suppress("HardCodedStringLiteral")
        LOG.info { "New pairing state: ${t::class.simpleName}" }
        _pairingState.value = Pairing(t)
    }

    private fun isTorActive(): Boolean {
        val plugin = pluginManager.getPlugin(TorConstants.ID) ?: return false
        return plugin.state == Plugin.State.ACTIVE
    }

    @UiExecutor
    fun onPairingLinkChanged(link: String) {
        _pairingLink.value = link
    }

    @UiExecutor
    fun pairMailbox(base32Link: String) {
        val payload = mailboxManager.convertBase32Payload(base32Link)
        if (isTorActive()) {
            pairingTask = mailboxManager.startPairingTask(payload).also {
                it.addObserver(this)
            }
        } else {
            _pairingState.value = OfflineWhenPairing
        }
    }

    @UiExecutor
    fun onPairingErrorSeen() {
        _pairingState.value = NotSetup
    }
}
