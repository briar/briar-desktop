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

import org.briarproject.briar.desktop.viewmodel.SingleStateEvent
import java.util.Locale

interface UnencryptedSettings {

    enum class Theme { AUTO, LIGHT, DARK }

    enum class Language {
        // special handling
        DEFAULT, EN,

        // languages as present in resources
        AR, BG, CA, DE, ES, FA, FR, GL, HE, HU, IS, IT, JA, KO, LT, MY, NL, PL, PT_BR, RO, RU, SK, SQ, SV, TR, UK, ZH_CN;

        val locale: Locale
            get() = if (this == DEFAULT)
                Locale.getDefault()
            else Locale.forLanguageTag(name.replace('_', '-'))
    }

    var theme: Theme
    var language: Language

    val invalidateScreen: SingleStateEvent<Unit>
}
