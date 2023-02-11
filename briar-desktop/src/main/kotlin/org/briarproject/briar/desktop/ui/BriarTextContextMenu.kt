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
import androidx.compose.foundation.ContextMenuItem
import androidx.compose.foundation.ContextMenuState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.text.TextContextMenu
import androidx.compose.foundation.text.TextContextMenu.TextManager
import androidx.compose.runtime.Composable
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n

@OptIn(ExperimentalFoundationApi::class)
object BriarTextContextMenu : TextContextMenu {
    @Composable
    override fun Area(textManager: TextManager, state: ContextMenuState, content: @Composable () -> Unit) {
        val items = {
            listOfNotNull(
                textManager.cut?.let {
                    ContextMenuItem(i18n("cut"), it)
                },
                textManager.copy?.let {
                    // don't show copy option if no text is selected
                    if (textManager.selectedText.isEmpty()) null
                    else ContextMenuItem(i18n("copy"), it)
                },
                textManager.paste?.let {
                    ContextMenuItem(i18n("paste"), it)
                },
                textManager.selectAll?.let {
                    ContextMenuItem(i18n("select_all"), it)
                },
            )
        }

        ContextMenuArea(items, state, content = content)
    }
}
