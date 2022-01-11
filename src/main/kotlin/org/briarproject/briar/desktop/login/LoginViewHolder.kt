package org.briarproject.briar.desktop.login

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import org.briarproject.bramble.api.account.AccountManager
import org.briarproject.bramble.api.crypto.DecryptionException
import org.briarproject.bramble.api.crypto.DecryptionResult.INVALID_PASSWORD
import org.briarproject.bramble.api.crypto.DecryptionResult.KEY_STRENGTHENER_ERROR
import org.briarproject.bramble.api.lifecycle.LifecycleManager.LifecycleState
import org.briarproject.briar.desktop.login.LoginViewHolder.State.COMPACTING
import org.briarproject.briar.desktop.login.LoginViewHolder.State.MIGRATING
import org.briarproject.briar.desktop.login.LoginViewHolder.State.SIGNED_OUT
import org.briarproject.briar.desktop.login.LoginViewHolder.State.STARTED
import org.briarproject.briar.desktop.login.LoginViewHolder.State.STARTING
import org.briarproject.briar.desktop.threading.BriarExecutors
import org.briarproject.briar.desktop.viewmodel.asState

class LoginViewHolder(
    private val viewModel: StartupViewModel,
    private val accountManager: AccountManager,
    private val briarExecutors: BriarExecutors,
    initialLifecycleState: LifecycleState,
) : StartupViewModel.ViewHolder {

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
                    }
                    _password.value = ""
                    _state.value = SIGNED_OUT
                }
            }
        }
    }
}
