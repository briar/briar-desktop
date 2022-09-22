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

import java.awt.AWTException
import java.awt.Image
import java.awt.SystemTray
import java.awt.SystemTray.getSystemTray
import java.awt.TrayIcon
import java.awt.event.ActionEvent
import java.io.IOException
import javax.imageio.ImageIO

class SystemTrayNotifications private constructor(image: Image?) {

    companion object {
        private var ourInstance: SystemTrayNotifications? = null

        @get:Throws(AWTException::class)
        @get:Synchronized
        val instance: SystemTrayNotifications?
            get() {
                if (ourInstance == null && SystemTray.isSupported()) {
                    ourInstance = SystemTrayNotifications(createImage())
                }
                return ourInstance
            }

        private fun createImage(): Image? {
            try {
                Thread.currentThread().contextClassLoader.getResourceAsStream("images/logo_circle.png")
                    .use { stream -> return ImageIO.read(stream) }
            } catch (e: IOException) {
                return null
            }
        }
    }

    private val trayIcon: TrayIcon

    init {
        val tooltip = "Briar" // NON-NLS
        trayIcon = TrayIcon(image, tooltip)
        trayIcon.isImageAutoSize = true
        getSystemTray().add(trayIcon)
        trayIcon.addActionListener { _: ActionEvent -> }
    }

    fun notify(title: String?, description: String?) {
        trayIcon.displayMessage(title, description, TrayIcon.MessageType.INFO)
    }

}