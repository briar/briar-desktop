package org.briarproject.briar.desktop.login

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import mu.KotlinLogging
import org.briarproject.bramble.api.account.AccountManager
import org.briarproject.bramble.api.crypto.PasswordStrengthEstimator
import org.briarproject.bramble.api.crypto.PasswordStrengthEstimator.QUITE_WEAK
import org.briarproject.bramble.api.identity.AuthorConstants.MAX_AUTHOR_NAME_LENGTH
import org.briarproject.bramble.api.lifecycle.IoExecutor
import org.briarproject.bramble.api.lifecycle.LifecycleManager
import org.briarproject.briar.desktop.login.RegistrationViewModel.State.CREATED
import org.briarproject.briar.desktop.login.RegistrationViewModel.State.CREATING
import org.briarproject.briar.desktop.login.RegistrationViewModel.State.INSERT_NICKNAME
import org.briarproject.briar.desktop.login.RegistrationViewModel.State.INSERT_PASSWORD
import org.briarproject.briar.desktop.threading.BriarExecutors
import org.briarproject.briar.desktop.viewmodel.ViewModel
import org.briarproject.briar.desktop.viewmodel.asState
import javax.inject.Inject

class RegistrationViewModel
@Inject
constructor(
    private val accountManager: AccountManager,
    private val briarExecutors: BriarExecutors,
    private val lifecycleManager: LifecycleManager,
    private val passwordStrengthEstimator: PasswordStrengthEstimator
) : ViewModel {

    companion object {
        private val LOG = KotlinLogging.logger {}
    }

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
                nickname.value.isNotEmpty() && !nicknameTooLongError.value
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

    @IoExecutor
    private fun signedIn() { // todo: factor out?
        val dbKey = accountManager.databaseKey ?: throw AssertionError()
        lifecycleManager.startServices(dbKey)
        lifecycleManager.waitForStartup()
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
                LOG.info { "Created account" }
                signedIn()
                _state.value = CREATED
            } else {
                LOG.warn { "Failed to create account" }
                _state.value = INSERT_NICKNAME
                // todo: show (meaningful) error to user
            }
        }
    }
}
