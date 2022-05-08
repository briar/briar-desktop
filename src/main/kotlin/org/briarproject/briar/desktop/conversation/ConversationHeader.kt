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

package org.briarproject.briar.desktop.conversation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
import androidx.compose.ui.unit.dp
import org.briarproject.briar.desktop.contact.ConnectionIndicator
import org.briarproject.briar.desktop.contact.ContactDropDown
import org.briarproject.briar.desktop.contact.ContactItem
import org.briarproject.briar.desktop.contact.ProfileCircle
import org.briarproject.briar.desktop.ui.Constants.HEADER_SIZE
import org.briarproject.briar.desktop.ui.HorizontalDivider
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n

@Composable
fun ConversationHeader(
    contactItem: ContactItem,
    onMakeIntroduction: () -> Unit,
    onDeleteAllMessages: () -> Unit,
    onChangeAlias: () -> Unit,
    onDeleteContact: () -> Unit,
) {
    val (menuState, setMenuState) = remember { mutableStateOf(ContactDropDown.State.CLOSED) }

    Box(modifier = Modifier.fillMaxWidth().height(HEADER_SIZE + 1.dp)) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth().align(Alignment.Center)
        ) {
            Row(modifier = Modifier.fillMaxHeight().padding(start = 8.dp).weight(1f, fill = false)) {
                Box(modifier = Modifier.align(Alignment.CenterVertically)) {
                    ProfileCircle(36.dp, contactItem)
                    ConnectionIndicator(
                        modifier = Modifier.align(Alignment.BottomEnd).size(12.dp),
                        isConnected = contactItem.isConnected,
                        notConnectedColor = MaterialTheme.colors.background,
                    )
                }
                Text(
                    contactItem.displayName,
                    modifier = Modifier.align(Alignment.CenterVertically).padding(start = 12.dp)
                        .weight(1f, fill = false),
                    maxLines = 2,
                    overflow = Ellipsis,
                    style = MaterialTheme.typography.h2
                )
            }
            IconButton(
                onClick = { setMenuState(ContactDropDown.State.MAIN) },
                modifier = Modifier.align(Alignment.CenterVertically).padding(end = 16.dp)
            ) {
                Icon(Icons.Filled.MoreVert, i18n("access.contact.menu"), modifier = Modifier.size(24.dp))
                ContactDropDown(
                    menuState,
                    setMenuState,
                    onMakeIntroduction,
                    onDeleteAllMessages,
                    onChangeAlias,
                    onDeleteContact
                )
            }
        }
        HorizontalDivider(modifier = Modifier.align(Alignment.BottomCenter))
    }
}
