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

import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Alignment.Companion.Start
import androidx.compose.ui.Alignment.Companion.Top
import androidx.compose.ui.Alignment.Companion.TopEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.text
import androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
import androidx.compose.ui.unit.dp
import org.briarproject.bramble.api.contact.ContactId
import org.briarproject.bramble.api.identity.AuthorId
import org.briarproject.briar.api.identity.AuthorInfo.Status
import org.briarproject.briar.desktop.ui.NumberBadge
import org.briarproject.briar.desktop.ui.TrustIndicatorLong
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18nF
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18nP
import org.briarproject.briar.desktop.utils.PreviewUtils.DropDownValues
import org.briarproject.briar.desktop.utils.PreviewUtils.preview
import org.briarproject.briar.desktop.utils.TimeUtils.getFormattedTimestamp
import org.briarproject.briar.desktop.utils.appendCommaSeparated
import org.briarproject.briar.desktop.utils.buildBlankAnnotatedString
import org.briarproject.briar.desktop.utils.getRandomAuthorInfo
import java.time.Instant

@Suppress("HardCodedStringLiteral")
fun main() = preview(
    "name" to "Paul",
    "alias" to "UI Master",
    "trustLevel" to DropDownValues(0, Status.values().filterNot { it == Status.NONE }.map { it.name }),
    "isConnected" to true,
    "isEmpty" to false,
    "unread" to 3,
    "timestamp" to Instant.now().toEpochMilli(),
) {
    val item = ContactItem(
        id = ContactId(0),
        authorId = AuthorId(getRandomIdPersistent()),
        authorInfo = getRandomAuthorInfo(Status.valueOf(getStringParameter("trustLevel"))),
        name = getStringParameter("name"),
        alias = getStringParameter("alias"),
        isConnected = getBooleanParameter("isConnected"),
        isEmpty = getBooleanParameter("isEmpty"),
        unread = getIntParameter("unread"),
        timestamp = getLongParameter("timestamp"),
    )
    ContactItemView(item)
}

@Composable
fun ContactItemView(
    contactItem: ContactItem,
    modifier: Modifier = Modifier,
) = Row(
    horizontalArrangement = spacedBy(8.dp),
    verticalAlignment = CenterVertically,
    modifier = modifier
        // allows content to be bottom-aligned
        .height(IntrinsicSize.Min)
        .semantics {
            text = getDescription(contactItem)
        }
) {
    Row(
        verticalAlignment = CenterVertically,
        horizontalArrangement = spacedBy(12.dp),
        modifier = Modifier.weight(1f, fill = true),
    ) {
        Box(Modifier.align(Top).padding(vertical = 8.dp)) {
            ProfileCircle(36.dp, contactItem)
            NumberBadge(
                num = contactItem.unread,
                modifier = Modifier.align(TopEnd).offset(6.dp, (-6).dp)
            )
        }
        ContactItemViewInfo(
            contactItem = contactItem,
        )
    }
    ConnectionIndicator(
        modifier = Modifier.padding(16.dp).requiredSize(16.dp),
        isConnected = contactItem.isConnected
    )
}

private fun getDescription(contactItem: ContactItem) = buildBlankAnnotatedString {
    append(i18nF("access.contact.with_name", contactItem.displayName))
    // todo: trust level!
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

@Composable
private fun ContactItemViewInfo(contactItem: ContactItem) = Column(
    horizontalAlignment = Start,
) {
    Spacer(Modifier.weight(1f, fill = true))
    Text(
        text = contactItem.displayName,
        style = MaterialTheme.typography.body1,
        maxLines = 3,
        overflow = Ellipsis,
    )
    Spacer(Modifier.weight(1f, fill = true).heightIn(min = 4.dp))
    ProvideTextStyle(MaterialTheme.typography.caption) {
        TrustIndicatorLong(contactItem.trustLevel)
    }
    Spacer(Modifier.height(4.dp))
    Text(
        text = if (contactItem.isEmpty) i18n("contacts.card.nothing")
        else getFormattedTimestamp(contactItem.timestamp),
        style = MaterialTheme.typography.caption,
    )
}
