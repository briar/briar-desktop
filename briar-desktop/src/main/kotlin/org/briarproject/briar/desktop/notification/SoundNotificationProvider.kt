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

import mu.KotlinLogging
import org.briarproject.briar.desktop.utils.AudioUtils.loadAudioFromResource
import org.briarproject.briar.desktop.utils.AudioUtils.play
import org.briarproject.briar.desktop.utils.KLoggerUtils.e
import javax.sound.sampled.Clip

object SoundNotificationProvider : NotificationProvider {

    private val LOG = KotlinLogging.logger {}

    private lateinit var sound: Clip

    override var available: Boolean = false
        private set

    override val errorMessage: String
        get() = ""

    override fun init() {
        try {
            sound = loadAudioFromResource("/audio/notification.wav") ?: throw Exception() // NON-NLS
            available = true
        } catch (ex: Exception) {
            LOG.e(ex) { "Error while loading notification sound" }
        }
    }

    override fun uninit() {
        if (available) {
            sound.close()
            available = false
        }
    }

    private fun playSound() {
        if (available) sound.play()
    }

    override fun notifyPrivateMessages(num: Int, contacts: Int) = playSound()
    override fun notifyForumPosts(num: Int, forums: Int) = playSound()
    override fun notifyPrivateGroupMessages(num: Int, groups: Int) = playSound()
    override fun notifyBlogPosts(num: Int) = playSound()
}
