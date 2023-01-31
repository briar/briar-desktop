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

package org.briarproject.briar.desktop.settings

import mu.KotlinLogging
import org.briarproject.bramble.api.lifecycle.IoExecutor
import org.briarproject.briar.desktop.settings.UnencryptedSettings.Language
import org.briarproject.briar.desktop.settings.UnencryptedSettings.Language.DEFAULT
import org.briarproject.briar.desktop.settings.UnencryptedSettings.Theme
import org.briarproject.briar.desktop.settings.UnencryptedSettings.Theme.AUTO
import org.briarproject.briar.desktop.utils.InternationalizationUtils
import org.briarproject.briar.desktop.utils.KLoggerUtils.e
import org.briarproject.briar.desktop.viewmodel.SingleStateEvent
import java.util.prefs.Preferences
import javax.inject.Inject
import kotlin.reflect.KProperty

const val PREF_THEME = "theme" // NON-NLS
const val PREF_LANG = "language" // NON-NLS

class UnencryptedSettingsImpl @Inject internal constructor() : UnencryptedSettings {

    companion object {
        private val LOG = KotlinLogging.logger {}
    }

    // used for unencrypted settings, namely theme and language
    private val prefs = Preferences.userNodeForPackage(this::class.java)

    override val invalidateScreen = SingleStateEvent<Unit>()

    override var theme by EnumEntry(PREF_THEME, AUTO, Theme::class.java)

    override var language by EnumEntry(PREF_LANG, DEFAULT, Language::class.java, ::updateLocale)

    init {
        updateLocale(language)
    }

    private fun updateLocale(language: Language) {
        InternationalizationUtils.locale = language.locale
    }

    private class EnumEntry<T : Enum<*>>(
        private val key: String,
        private val default: T,
        private val enumClass: Class<T>,
        private val onChange: (value: T) -> Unit = {}
    ) {
        private lateinit var current: T

        operator fun getValue(thisRef: UnencryptedSettingsImpl, property: KProperty<*>): T {
            if (!::current.isInitialized) {
                val value = thisRef.prefs.get(key, default.name)
                current = enumClass.enumConstants.find { it.name == value }
                    ?: run {
                        LOG.e { "Unexpected enum value for ${enumClass.simpleName}: $value" }
                        default
                    }
            }
            return current
        }

        @IoExecutor
        operator fun setValue(thisRef: UnencryptedSettingsImpl, property: KProperty<*>, value: T) {
            if (current == value) return

            current = value
            thisRef.prefs.put(key, value.name)
            thisRef.prefs.flush() // write preferences to disk
            onChange(value)
            thisRef.invalidateScreen.emit(Unit)
        }
    }
}
