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

package org.briarproject.briar.desktop.login

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import org.briarproject.bramble.api.account.AccountManager
import org.briarproject.bramble.api.crypto.DecryptionException
import org.briarproject.bramble.api.crypto.DecryptionResult.INVALID_PASSWORD
import org.briarproject.bramble.api.crypto.DecryptionResult.KEY_STRENGTHENER_ERROR
import org.briarproject.bramble.api.lifecycle.LifecycleManager.LifecycleState
import org.briarproject.briar.desktop.login.LoginSubViewModel.State.COMPACTING
import org.briarproject.briar.desktop.login.LoginSubViewModel.State.MIGRATING
import org.briarproject.briar.desktop.login.LoginSubViewModel.State.SIGNED_OUT
import org.briarproject.briar.desktop.login.LoginSubViewModel.State.STARTED
import org.briarproject.briar.desktop.login.LoginSubViewModel.State.STARTING
import org.briarproject.briar.desktop.threading.BriarExecutors
import org.briarproject.briar.desktop.viewmodel.asState

class LoginSubViewModel(
    private val viewModel: StartupViewModel,
    private val accountManager: AccountManager,
    private val briarExecutors: BriarExecutors,
    initialLifecycleState: LifecycleState,
) : StartupViewModel.SubViewModel {

    enum class State {
        SIGNED_OUT, STARTING, MIGRATING, COMPACTING, STARTED
    }

    private val _state = mutableStateOf(SIGNED_OUT)
    val state = _state.asState()

    private val _password = mutableStateOf("")
    val password = _password.asState()

    private val _passwordInvalidError = mutableStateOf(false)
    val passwordInvalidError = _passwordInvalidError.asState()

    private val _decryptionFailedError = mutableStateOf(false)
    val decryptionFailedError = _decryptionFailedError.asState()

    val buttonEnabled = derivedStateOf { password.value.isNotEmpty() }

    fun setPassword(password: String) {
        _password.value = password
        _passwordInvalidError.value = false
    }

    fun closeDecryptionFailedDialog() {
        _decryptionFailedError.value = false
    }

    init {
        lifecycleStateChanged(initialLifecycleState)
    }

    override fun lifecycleStateChanged(s: LifecycleState) {
        _state.value =
            if (accountManager.hasDatabaseKey()) {
                when {
                    s.isAfter(LifecycleState.STARTING_SERVICES) -> STARTED
                    s == LifecycleState.MIGRATING_DATABASE -> MIGRATING
                    s == LifecycleState.COMPACTING_DATABASE -> COMPACTING
                    else -> _state.value
                }
            } else {
                SIGNED_OUT
            }
    }

    fun deleteAccount() = briarExecutors.onIoThread {
        accountManager.deleteAccount()
        viewModel.showRegistration()
    }

    fun signIn() {
        if (!buttonEnabled.value) return

        _state.value = STARTING
        briarExecutors.onIoThread {
            try {
                accountManager.signIn(password.value)
                viewModel.startBriarCore()
            } catch (e: DecryptionException) {
                // failure, try again
                briarExecutors.onUiThread {
                    when (e.decryptionResult) {
                        INVALID_PASSWORD -> _passwordInvalidError.value = true
                        KEY_STRENGTHENER_ERROR -> _decryptionFailedError.value = true
                        else -> {}
                    }
                    _password.value = ""
                    _state.value = SIGNED_OUT
                }
            }
        }
    }
}
