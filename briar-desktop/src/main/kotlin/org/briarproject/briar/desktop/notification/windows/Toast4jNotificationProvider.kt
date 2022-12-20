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

import de.mobanisto.toast4j.ToastBuilder
import de.mobanisto.toast4j.ToastHandle
import de.mobanisto.toast4j.Toaster
import de.mobanisto.wintoast.WinToastTemplate.WinToastTemplateType
import mu.KotlinLogging
import org.briarproject.briar.desktop.BuildData
import org.briarproject.briar.desktop.notification.AbstractNotificationProvider
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n
import org.briarproject.briar.desktop.utils.KLoggerUtils.e

object Toast4jNotificationProvider : AbstractNotificationProvider() {

    private val LOG = KotlinLogging.logger {}

    private lateinit var toaster: Toaster

    override var available: Boolean = false
        private set

    private enum class Error { NONE, INIT }

    private var error = Error.NONE

    override val errorMessage: String
        get() = when (error) {
            Error.INIT -> i18n("settings.notifications.visual.error.toast4j.init")
            else -> ""
        }

    override fun init() {
        toaster = Toaster.forAumi(BuildData.WINDOWS_AUMI)
        available = toaster.initialize()
        if (!available) {
            error = Error.INIT
            LOG.e { "unable to initialize toast4j" }
            return
        }
    }

    override fun uninit() {
        currentToast?.hide()
    }

    private var currentToast: ToastHandle? = null

    override fun sendNotification(text: String) {
        currentToast?.hide()
        currentToast = toaster.showToast(
            ToastBuilder(WinToastTemplateType.ToastText01).setSilent()
                .setLine1(text).build()
        )
    }
}
