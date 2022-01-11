/*
 * Briar Desktop
 * Copyright (C) 2021-2022 The Briar Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
