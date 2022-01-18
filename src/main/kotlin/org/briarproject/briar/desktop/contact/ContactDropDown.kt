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

package org.briarproject.briar.desktop.contact

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n
import org.briarproject.briar.desktop.utils.getCoreFeatureFlags
import org.briarproject.briar.desktop.utils.getDesktopFeatureFlags

@Composable
fun ContactDropDown(
    expanded: Boolean,
    close: () -> Unit,
    onMakeIntroduction: () -> Unit,
    onDeleteAllMessages: () -> Unit,
    onDeleteContact: () -> Unit,
) {
    val coreFeatureFlags = getCoreFeatureFlags()
    val desktopFeatureFlags = getDesktopFeatureFlags()

    var connectionMode by remember { mutableStateOf(false) }
    var contactMode by remember { mutableStateOf(false) }
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = close,
    ) {
        DropdownMenuItem(onClick = { close(); onMakeIntroduction() }) {
            Text(i18n("contacts.dropdown.introduction"), fontSize = 14.sp)
        }
        if (coreFeatureFlags.shouldEnableDisappearingMessages()) {
            DropdownMenuItem(onClick = {}) {
                Text(i18n("contacts.dropdown.disappearing"), fontSize = 14.sp)
            }
        }
        DropdownMenuItem(onClick = { close(); onDeleteAllMessages() }) {
            Text(i18n("contacts.dropdown.delete.all"), fontSize = 14.sp)
        }
        if (desktopFeatureFlags.shouldEnableTransportSettings()) {
            DropdownMenuItem(onClick = { connectionMode = true; close() }) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        i18n("contacts.dropdown.connections"),
                        fontSize = 14.sp,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                    Icon(
                        Icons.Filled.ArrowRight,
                        i18n("access.contacts.dropdown.connections.expand"),
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
            }
        }
        DropdownMenuItem(onClick = { contactMode = true; close() }) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    i18n("contacts.dropdown.contact"),
                    fontSize = 14.sp,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Icon(
                    Icons.Filled.ArrowRight,
                    i18n("access.contacts.dropdown.contacts.expand"),
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        }
    }
    DropdownMenu(
        expanded = connectionMode,
        onDismissRequest = { connectionMode = false },
    ) {
        DropdownMenuItem(onClick = { false }) {
            Text(i18n("contacts.dropdown.connections.title"), fontSize = 12.sp)
        }
        DropdownMenuItem(onClick = { false }) {
            Text(i18n("contacts.dropdown.connections.bluetooth"), fontSize = 14.sp)
        }
        DropdownMenuItem(onClick = { false }) {
            Text(i18n("contacts.dropdown.connections.removable"), fontSize = 14.sp)
        }
    }
    DropdownMenu(
        expanded = contactMode,
        onDismissRequest = { contactMode = false },
    ) {
        DropdownMenuItem(onClick = { false }) {
            Text(i18n("contacts.dropdown.contact.title"), fontSize = 12.sp)
        }
        DropdownMenuItem(onClick = { false }, enabled = false) {
            Text(i18n("contacts.dropdown.contact.change"), fontSize = 14.sp)
        }
        DropdownMenuItem(onClick = { close(); onDeleteContact() }) {
            Text(i18n("contacts.dropdown.contact.delete"), fontSize = 14.sp)
        }
    }
}
