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
import org.briarproject.briar.desktop.utils.LogUtils
import java.util.logging.Level.ALL
import java.util.logging.Logger

internal class RunWithMultipleTemporaryAccounts(
    private val names: List<String>,
    val customization: List<BriarDesktopTestApp>.() -> Unit
) {

    companion object {
        private val LOG = Logger.getLogger(RunWithMultipleTemporaryAccounts::class.java.name)
    }

    private val apps = mutableListOf<BriarDesktopTestApp>()

    @OptIn(ExperimentalComposeUiApi::class)
    fun run() {
        LogUtils.setupLogging(ALL)

        for (i in names.indices) {
            val name = names[i]
            val app = app(name, DEFAULT_SOCKS_PORT + 2 * i, DEFAULT_CONTROL_PORT + 2 * i)
            apps.add(app)
        }

        customization(apps)

        application {
            for (app in apps) {
                start(app, this)
            }
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
