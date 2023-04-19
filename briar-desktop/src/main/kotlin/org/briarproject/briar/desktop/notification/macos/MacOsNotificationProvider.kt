/*
 * Briar Desktop
 * Copyright (C) 2023 The Briar Project
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
package org.briarproject.briar.desktop.notification.macos

import de.jangassen.jfa.foundation.Foundation
import de.jangassen.jfa.foundation.Foundation.invoke
import de.jangassen.jfa.foundation.Foundation.nsString
import org.briarproject.briar.desktop.Strings.APP_NAME
import org.briarproject.briar.desktop.notification.AbstractNotificationProvider

object MacOsNotificationProvider : AbstractNotificationProvider() {

    override var available: Boolean = true
        private set
    override val errorMessage: String
        get() = ""

    override fun init() {
        // nothing to initialize here
    }

    override fun uninit() {
        removeAllDeliveredNotifications()
    }

    @Suppress("HardCodedStringLiteral")
    override fun sendNotification(text: String) {
        removeAllDeliveredNotifications()
        val notification = invoke(Foundation.getObjcClass("NSUserNotification"), "new")
        invoke(notification, "setTitle:", nsString(APP_NAME))
        invoke(notification, "setInformativeText:", nsString(text))
        val center = invoke(Foundation.getObjcClass("NSUserNotificationCenter"), "defaultUserNotificationCenter")
        invoke(center, "deliverNotification:", notification)
    }

    private fun removeAllDeliveredNotifications() {
        val center = invoke(Foundation.getObjcClass("NSUserNotificationCenter"), "defaultUserNotificationCenter")
        invoke(center, "removeAllDeliveredNotifications")
    }
}
