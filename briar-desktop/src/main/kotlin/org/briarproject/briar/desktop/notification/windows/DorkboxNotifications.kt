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

package org.briarproject.briar.desktop.notification.windows

import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinUser
import org.briarproject.briar.desktop.notification.windows.NOTIFYICONDATA.Companion.NIIF_NONE
import org.briarproject.briar.desktop.notification.windows.NOTIFYICONDATA.Companion.NIIF_NOSOUND
import org.briarproject.briar.desktop.notification.windows.Shell32.NIM_DELETE
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption

class DorkboxNotifications {

    fun notify(title: String, message: String) {
        // Copy icon from resources to temporary file because LoadImage only works with files
        val tmp = File.createTempFile("briar", ".ico")
        Thread.currentThread().contextClassLoader.getResourceAsStream("images/logo_circle.ico").use {
            Files.copy(it, tmp.toPath(), StandardCopyOption.REPLACE_EXISTING)
        }
        val image =
            User32.INSTANCE.LoadImage(null, tmp.absolutePath, WinUser.IMAGE_ICON, 0, 0, WinUser.LR_LOADFROMFILE)

        val data = NOTIFYICONDATA()
        data.setBalloon(title, message, 10000, NIIF_NONE or NIIF_NOSOUND)
        val icon = WinDef.HICON(image)
        data.setIcon(icon)
        val ret = Shell32.Shell_NotifyIcon(Shell32.NIM_ADD, data)
        User32.INSTANCE.DestroyIcon(icon)
        println("return value: $ret")
    }

    fun init() = uninit()

    fun uninit() {
        val data = NOTIFYICONDATA()
        Shell32.Shell_NotifyIcon(NIM_DELETE, data)
    }
}
