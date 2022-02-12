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

import org.briarproject.briar.desktop.settings.UnencryptedSettings.Language
import org.briarproject.briar.desktop.settings.UnencryptedSettings.Language.DEFAULT
import org.briarproject.briar.desktop.settings.UnencryptedSettings.Theme
import org.briarproject.briar.desktop.settings.UnencryptedSettings.Theme.AUTO
import org.briarproject.briar.desktop.utils.InternationalizationUtils
import org.briarproject.briar.desktop.viewmodel.SingleStateEvent
import java.util.prefs.Preferences
import javax.inject.Inject

const val PREF_THEME = "theme"
const val PREF_LANG = "language"

class UnencryptedSettingsImpl @Inject internal constructor() : UnencryptedSettings {

    // used for unencrypted settings, namely theme and language
    private val prefs = Preferences.userNodeForPackage(this::class.java)

    override val invalidateScreen = SingleStateEvent<Unit>()

    override var theme: Theme
        get() = Theme.valueOf(prefs.get(PREF_THEME, AUTO.name))
        set(value) {
            prefs.put(PREF_THEME, value.name)
            prefs.flush() // write preferences to disk
            invalidateScreen.emit(Unit)
        }

    override var language: Language
        get() = Language.valueOf(prefs.get(PREF_LANG, DEFAULT.name))
        set(value) {
            prefs.put(PREF_LANG, value.name)
            prefs.flush() // write preferences to disk
            updateLocale(value)
            invalidateScreen.emit(Unit)
        }

    init {
        updateLocale(language)
    }

    private fun updateLocale(language: Language) {
        InternationalizationUtils.locale = language.locale
    }
}
