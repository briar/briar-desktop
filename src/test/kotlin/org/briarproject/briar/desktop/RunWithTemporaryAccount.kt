package org.briarproject.briar.desktop

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.application
import mu.KotlinLogging
import org.briarproject.bramble.BrambleCoreEagerSingletons
import org.briarproject.briar.BriarCoreEagerSingletons
import org.briarproject.briar.desktop.TestUtils.getDataDir
import org.briarproject.briar.desktop.utils.LogUtils
import java.nio.file.Files
import java.nio.file.attribute.PosixFilePermissions
import java.util.logging.Level.ALL

internal class RunWithTemporaryAccount(
    val createAccount: Boolean = true,
    val login: Boolean = true,
    val makeDirUnwritable: Boolean = false,
    val customization: BriarDesktopTestApp.() -> Unit = {}
) {

    companion object {
        private val LOG = KotlinLogging.logger {}
    }

    @OptIn(ExperimentalComposeUiApi::class)
    fun run() {
        LogUtils.setupLogging(ALL)

        val dataDir = getDataDir()
        LOG.info { "Using data directory '$dataDir'" }

        if (makeDirUnwritable) {
            val permissions = PosixFilePermissions.fromString("r--r--r--")
            Files.setPosixFilePermissions(dataDir, permissions)
        }

        val app =
            DaggerBriarDesktopTestApp.builder().desktopTestModule(
                DesktopTestModule(dataDir)
            ).build()

        app.getShutdownManager().addShutdownHook {
            LOG.info { "deleting temporary account at $dataDir" }
            org.apache.commons.io.FileUtils.deleteDirectory(dataDir.toFile())
        }

        // We need to load the eager singletons directly after making the
        // dependency graphs
        BrambleCoreEagerSingletons.Helper.injectEagerSingletons(app)
        BriarCoreEagerSingletons.Helper.injectEagerSingletons(app)

        val lifecycleManager = app.getLifecycleManager()
        val accountManager = app.getAccountManager()

        if (createAccount) {
            val password = "verySecret123!"
            accountManager.createAccount("alice", password)

            if (login) {
                val dbKey = accountManager.databaseKey ?: throw AssertionError()
                lifecycleManager.startServices(dbKey)
                lifecycleManager.waitForStartup()
            }
        }

        customization(app)

        application {
            app.getBriarUi().start {
                app.getBriarUi().stop()
                exitApplication()
            }
        }
    }
}
