package org.briarproject.briar.desktop

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.application
import org.briarproject.bramble.BrambleCoreEagerSingletons
import org.briarproject.briar.BriarCoreEagerSingletons
import org.briarproject.briar.desktop.TestUtils.getDataDir
import java.util.logging.Level.INFO
import java.util.logging.LogManager
import java.util.logging.Logger

internal class RunWithTemporaryAccount(val customization: BriarDesktopTestApp.() -> Unit) {

    companion object {
        private val LOG = Logger.getLogger(RunWithTemporaryAccount::class.java.name)
    }

    @OptIn(ExperimentalComposeUiApi::class)
    fun run() {
        LogManager.getLogManager().getLogger("").level = INFO

        val dataDir = getDataDir()
        LOG.info("Using data directory '$dataDir'")

        val app =
            DaggerBriarDesktopTestApp.builder().desktopTestModule(
                DesktopTestModule(dataDir)
            ).build()

        app.getShutdownManager().addShutdownHook {
            LOG.info("deleting temporary account at $dataDir")
            org.apache.commons.io.FileUtils.deleteDirectory(dataDir.toFile())
        }

        // We need to load the eager singletons directly after making the
        // dependency graphs
        BrambleCoreEagerSingletons.Helper.injectEagerSingletons(app)
        BriarCoreEagerSingletons.Helper.injectEagerSingletons(app)

        val lifecycleManager = app.getLifecycleManager()
        val accountManager = app.getAccountManager()

        val password = "verySecret123!"
        accountManager.createAccount("alice", password)

        val dbKey = accountManager.databaseKey ?: throw AssertionError()
        lifecycleManager.startServices(dbKey)
        lifecycleManager.waitForStartup()

        customization(app)

        // Creating test data happens on a background thread. As we do not get notified about updates to the conact
        // list yet, we need to wait a moment in order for that to finish (hopefully).
        Thread.sleep(1000)

        application {
            app.getBriarUi().start {
                app.getBriarUi().stop()
                exitApplication()
            }
        }
    }
}
