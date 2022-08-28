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

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
import androidx.compose.ui.unit.dp
import org.briarproject.bramble.api.contact.ContactId
import org.briarproject.bramble.api.identity.AuthorId
import org.briarproject.briar.desktop.theme.outline
import org.briarproject.briar.desktop.theme.selectedCard
import org.briarproject.briar.desktop.theme.surfaceVariant
import org.briarproject.briar.desktop.ui.Constants.HEADER_SIZE
import org.briarproject.briar.desktop.ui.HorizontalDivider
import org.briarproject.briar.desktop.ui.NumberBadge
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n
import org.briarproject.briar.desktop.utils.PreviewUtils.preview
import org.briarproject.briar.desktop.utils.TimeUtils.getFormattedTimestamp
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
}

@Composable
fun ContactCard(
    contactItem: BaseContactItem,
    onSel: () -> Unit,
    selected: Boolean,
    onRemovePending: () -> Unit,
    padding: PaddingValues = PaddingValues(0.dp),
) {
    val bgColor = if (selected) MaterialTheme.colors.selectedCard else MaterialTheme.colors.surfaceVariant

    Card(
        modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = HEADER_SIZE).clickable(onClick = onSel),
        shape = RoundedCornerShape(0.dp),
        backgroundColor = bgColor,
        contentColor = MaterialTheme.colors.onSurface
    ) {
        when (contactItem) {
            is ContactItem -> {
                RealContactRow(contactItem, padding)
            }
            is PendingContactItem -> {
                PendingContactRow(contactItem, onRemovePending, padding)
            }
        }
    }
    HorizontalDivider()
}

@Composable
private fun RealContactRow(contactItem: ContactItem, padding: PaddingValues) {
    val outlineColor = MaterialTheme.colors.outline
    val briarSecondary = MaterialTheme.colors.secondary
    val briarSurfaceVar = MaterialTheme.colors.surfaceVariant

    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.padding(padding)) {
        Row(
            modifier = Modifier.align(Alignment.CenterVertically).padding(start = 16.dp, end = 8.dp)
                .weight(1f, fill = false)
        ) {
            Box(modifier = Modifier.align(Alignment.CenterVertically)) {
                ProfileCircle(36.dp, contactItem)
                NumberBadge(
                    num = contactItem.unread,
                    modifier = Modifier.align(Alignment.TopEnd).offset(6.dp, (-6).dp)
                )
            }
            RealContactInfo(
                contactItem = contactItem,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
        Canvas(
            modifier = Modifier.size(24.dp).align(Alignment.CenterVertically),
            onDraw = {
                val size = 16.dp
                drawCircle(color = outlineColor, radius = size.toPx() / 2f)
                drawCircle(
                    color = if (contactItem.isConnected) briarSecondary else briarSurfaceVar,
                    radius = (size - 2.dp).toPx() / 2f
                )
            }
        )
    }
}

@Composable
private fun PendingContactRow(contactItem: PendingContactItem, onRemove: () -> Unit, padding: PaddingValues) {
    Row(horizontalArrangement = Arrangement.SpaceBetween) {
        Row(
            modifier = Modifier.align(Alignment.CenterVertically).padding(start = 16.dp, end = 8.dp)
                .weight(1f, fill = false)
        ) {
            ProfileCircle(36.dp)
            PendingContactInfo(
                contactItem = contactItem,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
        IconButton(
            onClick = onRemove,
            modifier = Modifier.padding(end = 4.dp).align(Alignment.CenterVertically)
        ) {
            Icon(Icons.Filled.Delete, i18n("access.contacts.pending.remove"))
        }
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
