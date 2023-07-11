/*
 * Briar Desktop
 * Copyright (C) 2021-2023 The Briar Project
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
    fun notifyPrivateMessages(num: Int, contacts: Int)
    fun notifyForumPosts(num: Int, forums: Int)
    fun notifyPrivateGroupMessages(num: Int, groups: Int)
    fun notifyBlogPosts(num: Int)
}

interface VisualNotificationProvider : NotificationProvider

object StubNotificationProvider : VisualNotificationProvider {
    override val available: Boolean
        get() = false

    override val errorMessage: String
        get() = i18n("settings.notifications.visual.error.unsupported")

    override fun init() {}
    override fun uninit() {}
    override fun notifyPrivateMessages(num: Int, contacts: Int) {}
    override fun notifyForumPosts(num: Int, forums: Int) {}
    override fun notifyPrivateGroupMessages(num: Int, groups: Int) {}
    override fun notifyBlogPosts(num: Int) {}
}
