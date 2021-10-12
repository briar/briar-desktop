package org.briarproject.briar.desktop.utils

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
            val perms = HashSet<PosixFilePermission>().apply {
                add(PosixFilePermission.OWNER_READ)
                add(PosixFilePermission.OWNER_WRITE)
                add(PosixFilePermission.OWNER_EXECUTE)
            }
            Files.setPosixFilePermissions(file, perms)
        } else if (OsUtils.isWindows()) {
            file.toFile().apply {
                setReadable(true, true)
                setWritable(true, true)
                setExecutable(true, true)
            }
        }
    }
}
