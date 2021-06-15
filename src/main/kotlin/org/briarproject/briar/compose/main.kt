package org.briarproject.briar.compose

import org.briarproject.bramble.BrambleCoreEagerSingletons
import org.briarproject.briar.BriarCoreEagerSingletons
import java.io.File
import java.io.File.separator
import java.io.IOException
import java.lang.System.getProperty
import java.nio.file.Files.setPosixFilePermissions
import java.nio.file.attribute.PosixFilePermission
import java.util.logging.Level
import java.util.logging.LogManager

fun main() {
    LogManager.getLogManager().getLogger("").level = Level.INFO

    val dataDir = getDataDir()
    val app =
        DaggerBriarSwingApp.builder().swingModule(
            SwingModule(
                dataDir
            )
        ).build()
    // We need to load the eager singletons directly after making the
    // dependency graphs
    BrambleCoreEagerSingletons.Helper.injectEagerSingletons(app)
    BriarCoreEagerSingletons.Helper.injectEagerSingletons(app)

    app.getUI().startBriar()
    app.getUI().startUI()
}

private fun getDataDir(): File {
    val file = File(getProperty("user.home") + separator + ".briar")
    if (!file.exists() && !file.mkdirs()) {
        throw IOException("Could not create directory: ${file.absolutePath}")
    } else if (!file.isDirectory) {
        throw IOException("Data dir is not a directory: ${file.absolutePath}")
    }
    val perms = HashSet<PosixFilePermission>()
    perms.add(PosixFilePermission.OWNER_READ)
    perms.add(PosixFilePermission.OWNER_WRITE)
    perms.add(PosixFilePermission.OWNER_EXECUTE)
    setPosixFilePermissions(file.toPath(), perms)
    return file
}