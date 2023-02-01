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

package org.briarproject.briar.desktop.login

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import mu.KotlinLogging
import org.briarproject.bramble.api.account.AccountManager
import org.briarproject.bramble.api.crypto.PasswordStrengthEstimator
import org.briarproject.bramble.api.crypto.PasswordStrengthEstimator.QUITE_WEAK
import org.briarproject.bramble.api.identity.AuthorConstants.MAX_AUTHOR_NAME_LENGTH
import org.briarproject.briar.desktop.login.RegistrationSubViewModel.State.CREATING
import org.briarproject.briar.desktop.login.RegistrationSubViewModel.State.INSERT_NICKNAME
import org.briarproject.briar.desktop.login.RegistrationSubViewModel.State.INSERT_PASSWORD
import org.briarproject.briar.desktop.threading.BriarExecutors
import org.briarproject.briar.desktop.utils.KLoggerUtils.i
import org.briarproject.briar.desktop.utils.KLoggerUtils.w
import org.briarproject.briar.desktop.viewmodel.asState

class RegistrationSubViewModel(
    private val viewModel: StartupViewModel,
    private val accountManager: AccountManager,
    private val briarExecutors: BriarExecutors,
    private val passwordStrengthEstimator: PasswordStrengthEstimator,
) : StartupViewModel.SubViewModel {

    companion object {
        private val LOG = KotlinLogging.logger {}
    }

    object RegistrationError : ErrorSubViewModel.Error

    enum class State {
        INSERT_NICKNAME, INSERT_PASSWORD, CREATING, CREATED
    }

    private val _state = mutableStateOf(INSERT_NICKNAME)
    val state = _state.asState()

    private val _nickname = mutableStateOf("")
    private val _password = mutableStateOf("")
    private val _passwordConfirmation = mutableStateOf("")

    val nickname = _nickname.asState()
    val password = _password.asState()
    val passwordConfirmation = _passwordConfirmation.asState()

    val passwordStrength = derivedStateOf {
        passwordStrengthEstimator.estimateStrength(_password.value)
    }

    val nicknameTooLongError = derivedStateOf {
        nickname.value.length > MAX_AUTHOR_NAME_LENGTH
    }

    val passwordTooWeakError = derivedStateOf {
        password.value.isNotEmpty() && passwordStrength.value < QUITE_WEAK
    }
    val passwordMatchError = derivedStateOf {
        passwordConfirmation.value.isNotEmpty() && password.value != passwordConfirmation.value
    }

    val buttonEnabled = derivedStateOf {
        when (_state.value) {
            INSERT_NICKNAME ->
                nickname.value.isNotBlank() && !nicknameTooLongError.value
            INSERT_PASSWORD ->
                password.value.isNotEmpty() && passwordConfirmation.value.isNotEmpty() &&
                    !passwordTooWeakError.value && !passwordMatchError.value
            else ->
                false
        }
    }

    val showBackButton = derivedStateOf { _state.value == INSERT_PASSWORD }

    fun setNickname(nickname: String) {
        _nickname.value = nickname
    }

    fun setPassword(password: String) {
        _password.value = password
    }

    fun setPasswordConfirmation(passwordConfirmation: String) {
        _passwordConfirmation.value = passwordConfirmation
    }

    fun goToPassword() {
        if (!buttonEnabled.value) return

        _state.value = INSERT_PASSWORD
    }

    fun goBack() {
        _state.value = INSERT_NICKNAME
    }

    fun signUp() {
        if (!buttonEnabled.value) return

        _state.value = CREATING
        briarExecutors.onIoThread {
            if (accountManager.createAccount(_nickname.value, _password.value)) {
                LOG.i { "Created account" }
                viewModel.startBriarCore()
            } else {
                LOG.w { "Failed to create account" }
                viewModel.showError(RegistrationError, true)
            }
        }
    }
}
