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
                connectApps(app1, app2)
            }
        }
    }

    internal fun connectApps(app1: BriarDesktopTestApp, app2: BriarDesktopTestApp) {
        val cm1 = app1.getContactManager()
        val cm2 = app2.getContactManager()
        val name1 = app1.getIdentityManager().localAuthor.name
        val name2 = app2.getIdentityManager().localAuthor.name
        cm1.addPendingContact(cm2.handshakeLink, name2)
        cm2.addPendingContact(cm1.handshakeLink, name1)
    }
}
