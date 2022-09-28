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

package org.briarproject.briar.desktop.settings

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import org.briarproject.bramble.api.account.AccountManager
import org.briarproject.bramble.api.crypto.DecryptionException
import org.briarproject.bramble.api.crypto.DecryptionResult
import org.briarproject.bramble.api.crypto.PasswordStrengthEstimator
import org.briarproject.bramble.api.crypto.PasswordStrengthEstimator.QUITE_WEAK
import org.briarproject.briar.desktop.threading.BriarExecutors
import org.briarproject.briar.desktop.viewmodel.asState
import javax.inject.Inject

class ChangePasswordSubViewModel
@Inject
constructor(
    private val briarExecutors: BriarExecutors,
    private val accountManager: AccountManager,
    private val passwordStrengthEstimator: PasswordStrengthEstimator,
) {

    sealed class DialogState {
        object Idle : DialogState()
        object Loading : DialogState()
        object Done : DialogState()
        class Error(val result: DecryptionResult) : DialogState()
    }

    private val _oldPassword = mutableStateOf("")
    private val _password = mutableStateOf("")
    private val _passwordConfirmation = mutableStateOf("")
    private val _dialogState = mutableStateOf<DialogState>(DialogState.Idle)

    val oldPassword = _oldPassword.asState()
    val password = _password.asState()
    val passwordConfirmation = _passwordConfirmation.asState()

    val passwordStrength = derivedStateOf {
        passwordStrengthEstimator.estimateStrength(_password.value)
    }

    val passwordTooWeakError = derivedStateOf {
        password.value.isNotEmpty() && passwordStrength.value < QUITE_WEAK
    }
    val passwordMatchError = derivedStateOf {
        passwordConfirmation.value.isNotEmpty() && password.value != passwordConfirmation.value
    }

    val buttonEnabled = derivedStateOf {
        password.value.isNotEmpty() && passwordConfirmation.value.isNotEmpty() &&
            !passwordTooWeakError.value && !passwordMatchError.value
    }

    val dialogState = _dialogState.asState()

    fun setOldPassword(password: String) {
        _oldPassword.value = password
    }

    fun setPassword(password: String) {
        _password.value = password
    }

    fun setPasswordConfirmation(passwordConfirmation: String) {
        _passwordConfirmation.value = passwordConfirmation
    }

    fun confirmChange() {
        if (!buttonEnabled.value) return

        _dialogState.value = DialogState.Loading
        briarExecutors.onIoThread {
            try {
                accountManager.changePassword(_oldPassword.value, _password.value)
                _dialogState.value = DialogState.Done
            } catch (e: DecryptionException) {
                _dialogState.value = DialogState.Error(e.decryptionResult)
            }
        }
    }

    fun reset() {
        setOldPassword("")
        setPassword("")
        setPasswordConfirmation("")
        _dialogState.value = DialogState.Idle
    }
}
