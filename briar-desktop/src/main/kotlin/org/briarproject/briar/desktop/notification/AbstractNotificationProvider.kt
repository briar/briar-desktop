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

import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18nF
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18nP

abstract class AbstractNotificationProvider : VisualNotificationProvider {

    internal abstract fun sendNotification(text: String)

    override fun notifyPrivateMessages(num: Int, contacts: Int) = sendNotification(
        if (contacts == 1)
            i18nP("notifications.message.private.one_chat", num)
        else
            i18nF("notifications.message.private.several_chats", num, contacts)
    )

    override fun notifyForumPosts(num: Int, forums: Int) = sendNotification(
        if (forums == 1)
            i18nP("notifications.message.forum.one_forum", num)
        else
            i18nF("notifications.message.forum.several_forums", num, forums)
    )

    override fun notifyPrivateGroupMessages(num: Int, groups: Int) = sendNotification(
        if (groups == 1)
            i18nP("notifications.message.group.one_group", num)
        else
            i18nF("notifications.message.group.several_groups", num, groups)
    )
}
