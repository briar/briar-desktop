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

package org.briarproject.briar.desktop.notification

import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n

interface NotificationProvider {
    /**
     * true, if the [NotificationProvider] has been successfully initialized
     * and is ready to show notifications. false otherwise
     */
    val available: Boolean

    /**
     * if [available] is false, contains a message explaining the problem
     * as shown to the user
     */
    val errorMessage: String

    fun init()
    fun uninit()
    fun notifyPrivateMessages(num: Int)
}

object StubNotificationProvider : NotificationProvider {
    override val available: Boolean
        get() = false

    override val errorMessage: String
        get() = i18n("settings.notifications.error.unsupported")

    override fun init() {}
    override fun uninit() {}
    override fun notifyPrivateMessages(num: Int) {}
}
