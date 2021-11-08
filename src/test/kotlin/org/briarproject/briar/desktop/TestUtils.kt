package org.briarproject.briar.desktop

import org.briarproject.briar.desktop.utils.FileUtils
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.absolute

object TestUtils {

    fun getDataDir(): Path {
        val dataDir = Files.createTempDirectory("briar")
        if (!Files.exists(dataDir)) {
            throw IOException("Could not create directory: ${dataDir.absolute()}")
        } else if (!Files.isDirectory(dataDir)) {
            throw IOException("Data dir is not a directory: ${dataDir.absolute()}")
        }
        FileUtils.setRWX(dataDir)
        return dataDir
    }

    internal fun List<BriarDesktopTestApp>.connectAll() {
        forEachIndexed { i, app1 ->
            forEachIndexed inner@{ k, app2 ->
                if (i >= k) return@inner
                val cm1 = app1.getContactManager()
                val cm2 = app2.getContactManager()
                val name1 = app1.getIdentityManager().localAuthor.name
                val name2 = app2.getIdentityManager().localAuthor.name
                cm1.addPendingContact(cm2.handshakeLink, name2)
                cm2.addPendingContact(cm1.handshakeLink, name1)
            }
        }
    }
}
