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

package org.briarproject.briar.desktop.utils

import com.ibm.icu.text.MessageFormat
import mu.KotlinLogging
import org.briarproject.briar.desktop.utils.KLoggerUtils.w
import java.util.Locale
import java.util.MissingResourceException
import java.util.ResourceBundle

/**
 * Helper functions around internationalization.
 *
 * Instead of using the default Locale, one can also use, e.g., `Locale("ar")` instead
 * or call `Locale.setDefault()`.
 */
object InternationalizationUtils {

    private val LOG = KotlinLogging.logger {}

    /**
     * The current [Locale] to be used for translations.
     * Will be set during startup according to the user settings.
     */
    var locale: Locale = Locale.getDefault()

    /**
     * Returns the translated text of the string identified with `key`
     */
    fun i18n(key: String): String =
        try {
            val resourceBundle = createResourceBundle()
            resourceBundle.getString(key)
        } catch (e: MissingResourceException) {
            LOG.w { "Missing string resource for key '$key'" }
            ""
        }

    /**
     * Returns the translated text of a string with plurals identified with `key`
     *
     * Example key:
     * {0, plural, one {You have {0} message} other {You have {0} messages}}
     *
     */
    fun i18nP(key: String, amount: Int): String =
        try {
            val pattern: String = i18n(key)
            val messageFormat = MessageFormat(pattern, locale)
            messageFormat.format(arrayOf(amount))
        } catch (e: IllegalArgumentException) {
            LOG.w { "Pattern does not match arguments for resource '$key' and locale '$locale" }
            ""
        }

    /**
     * Returns the translated text of a formatted string with
     */
    fun i18nF(key: String, vararg params: Any): String =
        try {
            val pattern: String = i18n(key)
            java.text.MessageFormat.format(pattern, *params)
        } catch (e: IllegalArgumentException) {
            LOG.w { "Pattern does not match arguments for resource '$key'" }
            ""
        }

    /**
     * Returns the resource bundle used for i18n at Briar Desktop
     */
    private fun createResourceBundle() =
        ResourceBundle.getBundle(
            "strings.BriarDesktop",
            if (locale.toLanguageTag() == "en") Locale.ROOT // NON-NLS
            else locale
        )
}
