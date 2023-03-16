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
import org.briarproject.briar.desktop.mailbox.MailboxPairingUiState.WasUnpaired
import org.briarproject.briar.desktop.threading.BriarExecutors
import org.briarproject.briar.desktop.threading.UiExecutor
import org.briarproject.briar.desktop.utils.KLoggerUtils.i
import org.briarproject.briar.desktop.utils.KLoggerUtils.w
import org.briarproject.briar.desktop.viewmodel.EventListenerDbViewModel
import org.briarproject.briar.desktop.viewmodel.asState
import javax.inject.Inject

sealed class MailboxPairingUiState {
    object Unknown : MailboxPairingUiState()
    object NotSetup : MailboxPairingUiState()
    class Pairing(val pairingState: MailboxPairingState) : MailboxPairingUiState()
    object OfflineWhenPairing : MailboxPairingUiState()
    class IsPaired(val connectionCheckRunning: Boolean, val isWiping: Boolean) : MailboxPairingUiState()
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

    private val _pairingUiState = mutableStateOf<MailboxPairingUiState>(Unknown)
    val pairingUiState = _pairingUiState.asState()
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
                briarExecutors.onUiThread {
                    _pairingUiState.value = IsPaired(connectionCheckRunning = false, isWiping = false)
                    _status.value = mailboxStatus
                }
            } else briarExecutors.onUiThread {
                _pairingUiState.value = NotSetup
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
        val lastState = _pairingUiState.value
        if (lastState is Pairing) {
            // check that we not just finished pairing (showing success screen)
            if (lastState.pairingState !is Paired) _pairingUiState.value = OfflineWhenPairing
            // else ignore offline event as user will be leaving UI flow anyway
        }
    }

    @UiExecutor
    override fun accept(t: MailboxPairingState) {
        LOG.i { "New pairing state: ${t::class.simpleName}" }
        _pairingUiState.value = Pairing(t)
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
    fun pairMailbox() {
        val base32Link = pairingLink.value
        val payload = mailboxManager.convertBase32Payload(base32Link)
        if (isTorActive()) {
            pairingTask = mailboxManager.startPairingTask(payload).also {
                it.addObserver(this)
            }
        } else {
            _pairingUiState.value = OfflineWhenPairing
        }
    }

    @UiExecutor
    fun onPairingErrorSeen() {
        _pairingUiState.value = NotSetup
    }

    @UiExecutor
    fun checkConnection() {
        // we can only check the connection when we are already paired (or just finished pairing)
        _pairingUiState.value = IsPaired(connectionCheckRunning = true, isWiping = false)
        briarExecutors.onIoThread {
            // this check updates _status state via an Event
            val success = mailboxManager.checkConnection()
            LOG.i { "Got result from connection check: $success" }
            briarExecutors.onUiThread {
                val s = pairingUiState.value
                if (s is IsPaired) _pairingUiState.value = IsPaired(connectionCheckRunning = false, isWiping = false)
                else LOG.w { "Unexpected state: ${s::class.simpleName}" }
            }
        }
    }

    @UiExecutor
    fun unlink() {
        _pairingUiState.value = IsPaired(connectionCheckRunning = false, isWiping = true)
        briarExecutors.onIoThread {
            val wasWiped = mailboxManager.unPair()
            briarExecutors.onUiThread {
                _pairingUiState.value = WasUnpaired(!wasWiped)
            }
        }
    }
}
