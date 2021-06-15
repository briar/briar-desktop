package org.briarproject.briar.compose

import org.briarproject.bramble.api.account.AccountManager
import org.briarproject.bramble.api.crypto.DecryptionException
import org.briarproject.bramble.api.crypto.PasswordStrengthEstimator
import org.briarproject.bramble.api.lifecycle.LifecycleManager
import javax.annotation.concurrent.Immutable
import javax.inject.Inject
import javax.inject.Singleton
import javax.swing.JOptionPane

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
            createAccount();
        } else {
            while (true) {
//                val password = PasswordPrompt.promptForPassword();
//                if (!password.isValid) {
//                    // this happens when dismissing the dialog or clicking 'cancel'
//                    exitProcess(1)
//                }
                val password = "sdifjasdjhfksjadf"
                try {
                    accountManager.signIn(password)
                    break
                } catch (e: DecryptionException) {
                    JOptionPane.showMessageDialog(
                        null, "Wrong password",
                        "Error", JOptionPane.ERROR_MESSAGE
                    )
                }
            }
        }
        val dbKey = accountManager.databaseKey ?: throw AssertionError()
        lifecycleManager.startServices(dbKey)
        lifecycleManager.waitForStartup()
    }

    override fun stop() {
        lifecycleManager.stopServices()
        lifecycleManager.waitForShutdown()
    }

    private fun createAccount() {
//        echo("No account found. Let's create one!\n\n")
//        val result = NewAccountPrompt.promptForDetails();
//        if (!result.isValid) {
//            echo("Error: Please enter a username and password")
//            exitProcess(1)
//        }
//        try {
//            check(passwordStrengthEstimator, result)
//        } catch (e: UsageError) {
//            return;
//        }

        accountManager.createAccount("Nico", "sdifjasdjhfksjadf")
    }

}
