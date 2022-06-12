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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.text
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
import androidx.compose.ui.unit.dp
import org.briarproject.bramble.api.contact.ContactId
import org.briarproject.bramble.api.contact.PendingContactId
import org.briarproject.bramble.api.contact.PendingContactState
import org.briarproject.bramble.api.identity.AuthorId
import org.briarproject.briar.desktop.theme.selectedCard
import org.briarproject.briar.desktop.ui.Constants.HEADER_SIZE
import org.briarproject.briar.desktop.ui.HorizontalDivider
import org.briarproject.briar.desktop.ui.MessageCounter
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18nF
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18nP
import org.briarproject.briar.desktop.utils.PreviewUtils.preview
import org.briarproject.briar.desktop.utils.TimeUtils.getFormattedTimestamp
import org.briarproject.briar.desktop.utils.appendCommaSeparated
import java.time.Instant

@Suppress("HardCodedStringLiteral")
fun main() = preview(
    "name" to "Paul",
    "alias" to "UI Master",
    "isConnected" to true,
    "isEmpty" to false,
    "unread" to 3,
    "timestamp" to Instant.now().toEpochMilli(),
    "selected" to false,
) {
    Column(Modifier.selectableGroup()) {
        ContactCard(
            ContactItem(
                idWrapper = RealContactIdWrapper(ContactId(0)),
                authorId = AuthorId(getRandomIdPersistent()),
                name = getStringParameter("name"),
                alias = getStringParameter("alias"),
                isConnected = getBooleanParameter("isConnected"),
                isEmpty = getBooleanParameter("isEmpty"),
                unread = getIntParameter("unread"),
                timestamp = getLongParameter("timestamp"),
                avatar = null,
            ),
            {}, getBooleanParameter("selected"), {}
        )
        ContactCard(
            PendingContactItem(
                idWrapper = PendingContactIdWrapper(PendingContactId(getRandomId())),
                alias = getStringParameter("alias"),
                timestamp = getLongParameter("timestamp"),
                state = PendingContactState.ADDING_CONTACT
            ),
            {}, false, {}
        )
    }
}

@Composable
fun ContactCard(
    contactItem: BaseContactItem,
    onSel: () -> Unit,
    selected: Boolean,
    onRemovePending: () -> Unit,
) {
    val bgColor = if (selected) MaterialTheme.colors.selectedCard else Color.Transparent

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = HEADER_SIZE)
            .semantics {
                contentDescription = if (selected) i18n("access.list.selected.yes")
                else i18n("access.list.selected.no")
                // todo: stateDescription apparently not used
                // stateDescription = if (selected) "selected" else "not selected"
            }
            .selectable(selected, onClick = onSel, role = Role.Button)
            .background(bgColor),
        verticalArrangement = Arrangement.Center
    ) {
        when (contactItem) {
            is ContactItem -> {
                RealContactRow(contactItem)
            }
            is PendingContactItem -> {
                PendingContactRow(contactItem, onRemovePending)
            }
        }
    }
    HorizontalDivider()
}

@Composable
private fun RealContactRow(contactItem: ContactItem) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                text = buildAnnotatedString {
                    append(i18nF("access.contact.with_name", contactItem.displayName))
                    appendCommaSeparated(
                        if (contactItem.isConnected) i18n("access.contact.connected.yes")
                        else i18n("access.contact.connected.no")
                    )
                    if (contactItem.unread > 0)
                        appendCommaSeparated(i18nP("access.contact.unread_count", contactItem.unread))
                    if (contactItem.isEmpty)
                        appendCommaSeparated(i18n("contacts.card.nothing"))
                    else
                        appendCommaSeparated(
                            i18nF(
                                "access.contact.last_message_timestamp",
                                getFormattedTimestamp(contactItem.timestamp)
                            )
                        )
                    append('.')
                }
            }
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, end = 8.dp).padding(vertical = 8.dp)
        ) {
            Box {
                ProfileCircle(36.dp, contactItem)
                MessageCounter(
                    unread = contactItem.unread,
                    modifier = Modifier.align(Alignment.TopEnd).offset(6.dp, (-6).dp)
                )
            }
            RealContactInfo(
                contactItem = contactItem,
            )
        }
        ConnectionIndicator(
            modifier = Modifier.padding(end = (16 + 4).dp).size(16.dp),
            isConnected = contactItem.isConnected
        )
    }
}

@Composable
private fun PendingContactRow(contactItem: PendingContactItem, onRemove: () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                text = buildAnnotatedString {
                    append(i18nF("access.contact.pending.with_name", contactItem.displayName))
                    // todo: include pending status
                    appendCommaSeparated(
                        i18nF(
                            "access.contact.pending.added_timestamp",
                            getFormattedTimestamp(contactItem.timestamp)
                        )
                    )
                }
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, end = 8.dp).padding(vertical = 8.dp)
        ) {
            ProfileCircle(36.dp)
            PendingContactInfo(
                contactItem = contactItem,
            )
        }
        IconButton(
            icon = Icons.Filled.Delete,
            contentDescription = i18n("access.contacts.pending.remove"),
            onClick = onRemove,
            modifier = Modifier.padding(end = 4.dp)
        )
    }
}

@Composable
private fun RealContactInfo(contactItem: ContactItem, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(start = 12.dp)) {
        Text(
            contactItem.displayName,
            style = MaterialTheme.typography.body1,
            maxLines = 3,
            overflow = Ellipsis,
            modifier = Modifier.align(Alignment.Start).padding(bottom = 2.dp)
        )
        Text(
            if (contactItem.isEmpty) i18n("contacts.card.nothing") else getFormattedTimestamp(
                contactItem.timestamp
            ),
            style = MaterialTheme.typography.caption,
            modifier = Modifier.align(Alignment.Start)
        )
    }
}

@Composable
private fun PendingContactInfo(contactItem: PendingContactItem, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(start = 12.dp)) {
        Text(
            contactItem.displayName,
            style = MaterialTheme.typography.body1,
            maxLines = 3,
            overflow = Ellipsis,
            modifier = Modifier.align(Alignment.Start).padding(bottom = 2.dp)
        )
        Text(
            getFormattedTimestamp(contactItem.timestamp),
            modifier = Modifier.align(Alignment.Start),
            style = MaterialTheme.typography.caption,
        )
    }
}
