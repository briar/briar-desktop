package org.briarproject.briar.desktop

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.application
import org.apache.commons.io.FileUtils
import org.briarproject.bramble.BrambleCoreEagerSingletons
import org.briarproject.bramble.util.OsUtils.isLinux
import org.briarproject.bramble.util.OsUtils.isMac
import org.briarproject.bramble.util.OsUtils.isWindows
import org.briarproject.briar.BriarCoreEagerSingletons
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Files.setPosixFilePermissions
import java.nio.file.Path
import java.nio.file.attribute.PosixFilePermission
import java.nio.file.attribute.PosixFilePermission.OWNER_EXECUTE
import java.nio.file.attribute.PosixFilePermission.OWNER_READ
import java.nio.file.attribute.PosixFilePermission.OWNER_WRITE
import java.util.logging.Level.INFO
import java.util.logging.LogManager
import kotlin.io.path.absolute

private class TestWithTemporaryAccount {

    @OptIn(ExperimentalComposeUiApi::class)
    fun run() = application {
        LogManager.getLogManager().getLogger("").level = INFO

        val dataDir = getDataDir()
        println("Using data directory '$dataDir'")

        val app =
            DaggerBriarDesktopTestApp.builder().desktopTestModule(
                DesktopTestModule(dataDir.toFile())
            ).build()

        app.getShutdownManager().addShutdownHook {
            println("deleting temporary account at $dataDir")
            FileUtils.deleteDirectory(dataDir.toFile())
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

        app.getDeterministicTestDataCreator().createTestData(5, 20, 50)
        app.getTestDataCreator().createTestData(5, 20, 50, 4, 4, 10)

        // Creating test data happens on a background thread. As we do not get notified about updates to the conact
        // list yet, we need to wait a moment in order for that to finish (hopefully).
        Thread.sleep(1000)

        app.getUI().startBriar()
    }

    private fun getDataDir(): Path {
        val dataDir = Files.createTempDirectory("briar")
        if (!Files.exists(dataDir)) {
            throw IOException("Could not create directory: ${dataDir.absolute()}")
        } else if (!Files.isDirectory(dataDir)) {
            throw IOException("Data dir is not a directory: ${dataDir.absolute()}")
        }
        if (isLinux() || isMac()) {
            val perms = HashSet<PosixFilePermission>()
            perms.add(OWNER_READ)
            perms.add(OWNER_WRITE)
            perms.add(OWNER_EXECUTE)
            setPosixFilePermissions(dataDir, perms)
        } else if (isWindows()) {
            val file = dataDir.toFile()
            file.setReadable(true, true)
            file.setWritable(true, true)
            file.setExecutable(true, true)
        }
        return dataDir
    }
}

fun main(args: Array<String>) = TestWithTemporaryAccount().run()
