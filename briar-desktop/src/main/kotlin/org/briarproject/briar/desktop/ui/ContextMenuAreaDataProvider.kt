/*
 * Briar Desktop
 * Copyright (C) 2023 The Briar Project
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

import androidx.compose.foundation.ContextMenuArea
import androidx.compose.foundation.ContextMenuDataProvider
import androidx.compose.foundation.ContextMenuItem
import androidx.compose.foundation.LocalContextMenuRepresentation
import androidx.compose.runtime.Composable

/**
 * Defines a container where context menu is available. Menu is triggered by right mouse clicks.
 * Representation of menu is defined by [LocalContextMenuRepresentation]`
 *
 * Different to [ContextMenuArea], this area also merges the [items] with descendant text context menus.
 *
 * @param items List of context menu items. Final context menu contains all items from descendant
 * [ContextMenuArea] and [ContextMenuDataProvider].
 * @param content The content of the [ContextMenuAreaDataProvider].
 */
@Composable
fun ContextMenuAreaDataProvider(
    items: () -> List<ContextMenuItem>,
    content: @Composable () -> Unit,
) = ContextMenuDataProvider(items) {
    ContextMenuArea(items = { emptyList() }, content = content)
}
