package org.briarproject.briar.desktop

import org.briarproject.bramble.util.OsUtils
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.PosixFilePermission

object FileUtils {

    /**
     * Sets read, write and executable flags for the specified file in a platform independent manner.
     */
    fun setRWX(file: Path) {
        if (OsUtils.isLinux() || OsUtils.isMac()) {
            val perms = HashSet<PosixFilePermission>()
            perms.add(PosixFilePermission.OWNER_READ)
            perms.add(PosixFilePermission.OWNER_WRITE)
            perms.add(PosixFilePermission.OWNER_EXECUTE)
            Files.setPosixFilePermissions(file, perms)
        } else if (OsUtils.isWindows()) {
            val f = file.toFile()
            f.setReadable(true, true)
            f.setWritable(true, true)
            f.setExecutable(true, true)
        }
    }
}
