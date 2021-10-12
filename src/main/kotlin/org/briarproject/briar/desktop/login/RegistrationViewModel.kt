package org.briarproject.briar.desktop.login

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import org.briarproject.bramble.api.account.AccountManager
import org.briarproject.bramble.api.crypto.PasswordStrengthEstimator
import org.briarproject.bramble.api.lifecycle.LifecycleManager
import javax.inject.Inject

class RegistrationViewModel
@Inject
constructor(
    private val accountManager: AccountManager,
    private val lifecycleManager: LifecycleManager,
    private val passwordStrengthEstimator: PasswordStrengthEstimator,
) {

    private var isSafeEnough = mutableStateOf(false)
    private val _username = mutableStateOf("")
    private val _password = mutableStateOf("")

    val username: State<String> = _username
    val password: State<String> = _password

    fun setUsername(username: String) {
        _username.value = username
    }

    fun setPassword(password: String) {
        _password.value = password
        // TODO: decide on useful value here
        isSafeEnough.value = passwordStrengthEstimator.estimateStrength(password) > 0
    }

    fun signUp(success: () -> Unit) {
        accountManager.createAccount(_username.value, _password.value)
        signedIn()
        success()
    }

    private fun signedIn() {
        val dbKey = accountManager.databaseKey ?: throw AssertionError()
        lifecycleManager.startServices(dbKey)
        lifecycleManager.waitForStartup()
    }
}
