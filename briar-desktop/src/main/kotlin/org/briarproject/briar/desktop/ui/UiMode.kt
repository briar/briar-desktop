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

package org.briarproject.briar.desktop.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChromeReaderMode
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WifiTethering
import androidx.compose.ui.graphics.vector.ImageVector

enum class UiMode(val icon: ImageVector, val contentDescriptionKey: String) {
    CONTACTS(Icons.Filled.Contacts, "access.mode.contacts"),
    GROUPS(Icons.Filled.Group, "access.mode.groups"),
    FORUMS(Icons.Filled.Forum, "access.mode.forums"),
    BLOGS(Icons.Filled.ChromeReaderMode, "access.mode.blogs"),
    TRANSPORTS(Icons.Filled.WifiTethering, "access.mode.transports"),
    SETTINGS(Icons.Filled.Settings, "access.mode.settings"),
    ABOUT(Icons.Filled.Info, "access.mode.about"),
}
