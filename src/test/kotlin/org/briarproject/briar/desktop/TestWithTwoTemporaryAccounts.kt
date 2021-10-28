package org.briarproject.briar.desktop

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.application
import org.briarproject.bramble.BrambleCoreEagerSingletons
import org.briarproject.briar.BriarCoreEagerSingletons
import org.briarproject.briar.desktop.utils.FileUtils
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.logging.Level.INFO
import java.util.logging.LogManager
import java.util.logging.Logger
import kotlin.io.path.absolute

fun main(args: Array<String>) = TestWithTwoTemporaryAccounts().run()

internal class TestWithTwoTemporaryAccounts() {

    companion object {
        private val LOG = Logger.getLogger(TestWithTwoTemporaryAccounts::class.java.name)
    }

    @OptIn(ExperimentalComposeUiApi::class)
    fun run() {
        LogManager.getLogManager().getLogger("").level = INFO

        application {
            app(this, "alice")
            app(this, "bob")
        }
    }

    @Composable
    private fun app(applicationScope: ApplicationScope, name: String) {
        val dataDir = getDataDir()
        LOG.info("Using data directory '$dataDir'")

        val app =
            DaggerBriarDesktopTestApp.builder().desktopTestModule(
                DesktopTestModule(dataDir.toFile())
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

        app.getDeterministicTestDataCreator().createTestData(5, 20, 50)

        // Creating test data happens on a background thread. As we do not get notified about updates to the conact
        // list yet, we need to wait a moment in order for that to finish (hopefully).
        Thread.sleep(1000)

        app.getBriarUi().start(applicationScope)
    }

    private fun getDataDir(): Path {
        val dataDir = Files.createTempDirectory("briar")
        if (!Files.exists(dataDir)) {
            throw IOException("Could not create directory: ${dataDir.absolute()}")
        } else if (!Files.isDirectory(dataDir)) {
            throw IOException("Data dir is not a directory: ${dataDir.absolute()}")
        }
        FileUtils.setRWX(dataDir)
        return dataDir
    }
}
