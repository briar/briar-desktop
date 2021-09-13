package org.briarproject.briar.desktop

import androidx.compose.desktop.Window
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import org.briarproject.bramble.api.account.AccountManager
import org.briarproject.bramble.api.crypto.DecryptionException
import org.briarproject.bramble.api.crypto.PasswordStrengthEstimator
import org.briarproject.bramble.api.lifecycle.LifecycleManager
import org.briarproject.briar.desktop.dialogs.Login
import org.briarproject.briar.desktop.dialogs.Registration
import org.briarproject.briar.desktop.paul.views.BriarUIStateManager
import javax.annotation.concurrent.Immutable
import javax.inject.Inject
import javax.inject.Singleton

interface BriarService {
    @Composable
    fun start()
    fun stop()
}

@Immutable
@Singleton
internal class BriarServiceImpl
@Inject
constructor(
    private val accountManager: AccountManager,
    private val lifecycleManager: LifecycleManager,
    private val passwordStrengthEstimator: PasswordStrengthEstimator
) : BriarService {

    @Composable
    override fun start() {
        if (!accountManager.accountExists()) {
            createAccount()
        } else {
            login()
        }
    }

    override fun stop() {
        lifecycleManager.stopServices()
        lifecycleManager.waitForShutdown()
    }

    private fun createAccount() {
        print("No account found. Let's create one!\n\n")
        Registration("Briar", accountManager, lifecycleManager)
    }

    @Composable
    private fun login() {
        val title = "Briar Desktop"
        var screenState by remember { mutableStateOf<Screen>(Screen.Login) }
        Window(title = title) {
            when (val screen = screenState) {
                is Screen.Login ->
                    Login(
                        "Briar",
                        onResult = {
                            try {
                                accountManager.signIn(it)
                                signedIn()
                                screenState = Screen.Main
                            } catch (e: DecryptionException) {
                                // failure, try again
                            }
                        }
                    )

                is Screen.Main ->
                    BriarUIStateManager()
            }
        }
    }

    private fun signedIn() {
        val dbKey = accountManager.databaseKey ?: throw AssertionError()
        lifecycleManager.startServices(dbKey)
        lifecycleManager.waitForStartup()
    }
}
