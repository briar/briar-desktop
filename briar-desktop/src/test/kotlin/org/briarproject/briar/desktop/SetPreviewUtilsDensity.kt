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

package org.briarproject.briar.desktop

import org.briarproject.briar.desktop.utils.PreviewUtils
import java.util.prefs.Preferences

/**
 * This executable stores a custom density used for UI previews created using [PreviewUtils] into the user settings.
 * On hidpi Linux devices it makes sense to set this to some value once that makes the previews appear big enough from
 * then on.
 * We're using a different dedicated preference node in order to keep this independent of the settings used in Briar
 * itself.
 */
fun main() {
    val prefs = Preferences.userNodeForPackage(PreviewUtils::class.java)
    prefs.put("previewsUiScale", "2.0")
    prefs.flush()
}
