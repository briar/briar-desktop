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

import com.sun.jna.Library
import com.sun.jna.Native
import com.sun.jna.Pointer
import mu.KotlinLogging
import org.briarproject.briar.desktop.notification.NotificationProvider
import org.briarproject.briar.desktop.utils.AudioUtils.loadAudioFromResource
import org.briarproject.briar.desktop.utils.AudioUtils.play
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18nP
import org.briarproject.briar.desktop.utils.KLoggerUtils.e
import javax.sound.sampled.Clip

object LibnotifyNotificationProvider : NotificationProvider {

    private val LOG = KotlinLogging.logger {}

    private var libNotifyAvailable: Boolean = false
    private var soundAvailable: Boolean = false

    private lateinit var libNotify: LibNotify

    private lateinit var sound: Clip

    override val available: Boolean
        get() = libNotifyAvailable

    private enum class Error { NONE, LOAD, INIT }

    private var error = Error.NONE

    override val errorMessage: String
        get() = when (error) {
            Error.LOAD -> i18n("settings.notifications.error.libnotify.load")
            Error.INIT -> i18n("settings.notifications.error.libnotify.init")
            else -> ""
        }

    override fun init() {
        try {
            sound = loadAudioFromResource("/audio/notification.wav") ?: throw Exception() // NON-NLS
            soundAvailable = true
        } catch (ex: Exception) {
            LOG.e(ex) { "Error while loading notification sound" }
        }

        try {
            libNotify = Native.load("libnotify.so.4", LibNotify::class.java) // NON-NLS
        } catch (err: UnsatisfiedLinkError) {
            error = Error.LOAD
            LOG.e { "unable to load libnotify" }
            return
        }

        libNotifyAvailable = libNotify.notify_init(i18n("main.title"))
        if (!libNotifyAvailable) {
            error = Error.INIT
            LOG.e { "unable to initialize libnotify" }
            return
        }

        /*
        val list = libNotify.notify_get_server_caps()
        for (i in 0 until libNotify.g_list_length(list)) {
            val cap = libNotify.g_list_nth_data(list, i).getString(0)
            println(cap)
            // todo: check for sound support?
        }
        */
    }

    override fun uninit() {
        if (libNotifyAvailable) {
            libNotify.notify_uninit()
            libNotifyAvailable = false
        }
        if (soundAvailable) {
            sound.close()
            soundAvailable = false
        }
    }

    override fun notifyPrivateMessages(num: Int) {
        if (!libNotifyAvailable) {
            // play sound even if libnotify unavailable
            if (soundAvailable) sound.play()
            return
        }

        /**
         * summary
         *
         * This is a single line overview of the notification.
         * For instance, "You have mail" or "A friend has come online".
         * It should generally not be longer than 40 characters, though this is not a requirement,
         * and server implementations should word wrap if necessary.
         * The summary must be encoded using UTF-8.
         */
        // todo: we could use body instead with markup (where supported)
        val text = i18nP("notifications.message.private", num)
        val notification = libNotify.notify_notification_new(text, null, null)

        /**
         * desktop-entry
         *
         * This specifies the name of the desktop filename representing the calling program.
         * This should be the same as the prefix used for the application's .desktop file.
         * An example would be "rhythmbox" from "rhythmbox.desktop".
         * This can be used by the daemon to retrieve the correct icon for the application, for logging purposes, etc.
         */
        // todo: desktop file usually not present for jar file, provide app_icon/image instead?
        libNotify.notify_notification_set_desktop_entry(notification, "org.briarproject.Briar")

        /**
         * suppress-sound
         *
         * Causes the server to suppress playing any sounds, if it has that ability.
         * This is usually set when the client itself is going to play its own sound.
         */
        libNotify.notify_notification_set_suppress_sound(notification, true)

        /**
         * category
         *
         * The type of notification this is: A received instant message notification.
         */
        libNotify.notify_notification_set_category(notification, "im.received")

        if (!libNotify.notify_notification_show(notification, null)) {
            // todo: error handling
            LOG.e { "error while sending notification via libnotify" }
        }

        sound.play()
    }

    /**
     * Functions as defined in the source code at
     * https://www.freedesktop.org/software/gstreamer-sdk/data/docs/latest/glib/glib-GVariant.html
     * https://www.freedesktop.org/software/gstreamer-sdk/data/docs/latest/glib/glib-Doubly-Linked-Lists.html
     * https://gitlab.gnome.org/GNOME/libnotify/-/tree/master/libnotify
     */
    @Suppress("FunctionName")
    private interface LibNotify : Library {
        fun g_list_length(list: Pointer): Int
        fun g_list_nth_data(list: Pointer, n: Int): Pointer

        /**
         *  Creates a new boolean GVariant instance -- either TRUE or FALSE.
         *
         *  @param value a gboolean value
         *
         *  @return a floating reference to a new boolean GVariant instance. [transfer none]
         *
         *  @since 2.24
         */
        fun g_variant_new_boolean(value: Boolean): Pointer

        /**
         *  Creates a string GVariant with the contents of [string].
         *
         *  @param string a normal utf8 nul-terminated string
         *
         *  @return a floating reference to a new string GVariant instance. [transfer none]
         *
         *  @since 2.24
         */
        fun g_variant_new_string(string: String): Pointer

        /**
         * Initialize libnotify. This must be called before any other functions.
         *
         * @param app_name The name of the application initializing libnotify.
         *
         * @return true if successful, or false on error.
         */
        fun notify_init(app_name: String): Boolean

        /**
         * Uninitialize libnotify.
         *
         * This should be called when the program no longer needs libnotify for
         * the rest of its lifecycle, typically just before exiting.
         */
        fun notify_uninit()

        /**
         * Synchronously queries the server for its capabilities and returns them in a #GList.
         *
         * @return [Pointer] to a #GList of server capability strings. Free
         *   the list elements with g_free() and the list itself with g_list_free().
         */
        fun notify_get_server_caps(): Pointer

        /**
         * Creates a new #NotifyNotification. The summary text is required, but
         * all other parameters are optional.
         *
         * @param summary The required summary text.
         * @param body The optional body text.
         * @param icon The optional icon theme icon name or filename.
         *
         * @return [Pointer] to the new #NotifyNotification.
         */
        fun notify_notification_new(summary: String, body: String?, icon: String?): Pointer

        /**
         * Tells the notification server to display the notification on the screen.
         *
         * @param notification [Pointer] to the notification.
         * @param error The returned error information.
         *
         * @return true if successful. On error, this will return false and set [error].
         */
        fun notify_notification_show(notification: Pointer, error: Pointer?): Boolean

        /**
         * Sets a hint for [key] with value [value]. If [value] is null,
         * a previously set hint for [key] is unset.
         *
         * If [value] is floating, it is consumed.
         *
         * @param notification [Pointer] to a #NotifyNotification
         * @param key the hint key
         * @param value [Pointer] to hint value as GVariant, or null to unset the hint
         *
         * @since 0.6
         */
        fun notify_notification_set_hint(notification: Pointer, key: String, value: Pointer?)

        /**
         * Sets the category of this notification. This can be used by the
         * notification server to filter or display the data in a certain way.
         *
         * @param notification [Pointer] to the notification.
         * @param category The category.
         */
        fun notify_notification_set_category(notification: Pointer, category: String)
    }

    @Suppress("FunctionName")
    private fun LibNotify.notify_notification_set_desktop_entry(notification: Pointer, desktopEntry: String) {
        val string = g_variant_new_string(desktopEntry)
        notify_notification_set_hint(notification, "desktop-entry", string) // NON-NLS
    }

    @Suppress("FunctionName")
    private fun LibNotify.notify_notification_set_suppress_sound(notification: Pointer, suppressSound: Boolean) {
        val bool = g_variant_new_boolean(suppressSound)
        notify_notification_set_hint(notification, "suppress-sound", bool) // NON-NLS
    }
}
