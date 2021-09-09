package org.briarproject.briar.desktop

import org.briarproject.bramble.api.account.AccountManager
import org.briarproject.bramble.api.crypto.PasswordStrengthEstimator
import org.briarproject.bramble.api.lifecycle.LifecycleManager
import org.briarproject.briar.desktop.dialogs.Login
import org.briarproject.briar.desktop.dialogs.Registration
import javax.annotation.concurrent.Immutable
import javax.inject.Inject
import javax.inject.Singleton

interface BriarService {
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

    private fun login() {
        Login("Briar", accountManager, lifecycleManager)
    }
}
