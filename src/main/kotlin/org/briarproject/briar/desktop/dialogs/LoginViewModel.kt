package org.briarproject.briar.desktop.dialogs

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import org.briarproject.bramble.api.account.AccountManager
import org.briarproject.bramble.api.crypto.DecryptionException
import org.briarproject.bramble.api.lifecycle.LifecycleManager
import javax.inject.Inject

class LoginViewModel
@Inject
constructor(
    private val accountManager: AccountManager,
    private val lifecycleManager: LifecycleManager,
) {

    private val _password = mutableStateOf("")

    val password: State<String> = _password

    fun setPassword(password: String) {
        _password.value = password
    }

    fun signIn(success: () -> Unit) {
        try {
            accountManager.signIn(password.value)
            signedIn()
            success()
        } catch (e: DecryptionException) {
            // failure, try again
        }
    }

    private fun signedIn() {
        val dbKey = accountManager.databaseKey ?: throw AssertionError()
        lifecycleManager.startServices(dbKey)
        lifecycleManager.waitForStartup()
    }
}
