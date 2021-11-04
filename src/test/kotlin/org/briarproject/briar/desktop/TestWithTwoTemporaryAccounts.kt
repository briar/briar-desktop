package org.briarproject.briar.desktop

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.application
import org.briarproject.bramble.BrambleCoreEagerSingletons
import org.briarproject.bramble.api.plugin.TorConstants.DEFAULT_CONTROL_PORT
import org.briarproject.bramble.api.plugin.TorConstants.DEFAULT_SOCKS_PORT
import org.briarproject.briar.BriarCoreEagerSingletons
import org.briarproject.briar.desktop.TestUtils.getDataDir
import java.util.logging.Level.INFO
import java.util.logging.LogManager
import java.util.logging.Logger

fun main(args: Array<String>) = TestWithTwoTemporaryAccounts().run()

internal class TestWithTwoTemporaryAccounts() {

    companion object {
        private val LOG = Logger.getLogger(TestWithTwoTemporaryAccounts::class.java.name)
    }

    private val apps = mutableListOf<BriarDesktopTestApp>()

    @OptIn(ExperimentalComposeUiApi::class)
    fun run() {
        LogManager.getLogManager().getLogger("").level = INFO

        val app1 = app("alice", DEFAULT_SOCKS_PORT, DEFAULT_CONTROL_PORT)
        val app2 = app("bob", DEFAULT_SOCKS_PORT + 2, DEFAULT_CONTROL_PORT + 2)
        apps.add(app1)
        apps.add(app2)
        app1.getDeterministicTestDataCreator().createTestData(5, 20, 50)
        app2.getDeterministicTestDataCreator().createTestData(5, 20, 50)

        application {
            start(app1, this)
            start(app2, this)
        }
    }

    private fun app(name: String, socksPort: Int, controlPort: Int): BriarDesktopTestApp {
        val dataDir = getDataDir()
        LOG.info("Using data directory '$dataDir'")

        val app =
            DaggerBriarDesktopTestApp.builder().desktopTestModule(
                DesktopTestModule(dataDir, socksPort, controlPort)
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
        accountManager.createAccount(name, password)

        val dbKey = accountManager.databaseKey ?: throw AssertionError()
        lifecycleManager.startServices(dbKey)
        lifecycleManager.waitForStartup()

        return app
    }

    @Composable
    fun start(app: BriarDesktopTestApp, applicationScope: ApplicationScope) {
        app.getBriarUi().start {
            apps.forEach {
                it.getBriarUi().stop()
            }
            applicationScope.exitApplication()
        }
    }
}
