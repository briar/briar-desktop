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
package org.briarproject.briar.desktop.notification.linux

/**
 * Small program to test notification support for Linux.
 * Build with ./gradlew :notificationTest
 * and run as java -jar briar-desktop/build/libs/briar-desktop-*-notificationTest.jar
 */
fun main() {
    LibnotifyNotificationProvider.apply {
        init()

        notifyPrivateMessages(4, 1)
        Thread.sleep(1000)

        notifyPrivateMessages(10, 3)
        Thread.sleep(1000)

        uninit()
    }
}
