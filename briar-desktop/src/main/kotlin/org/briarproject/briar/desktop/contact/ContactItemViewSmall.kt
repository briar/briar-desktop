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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.text
import androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
import androidx.compose.ui.unit.dp
import org.briarproject.bramble.api.contact.ContactId
import org.briarproject.bramble.api.identity.AuthorId
import org.briarproject.briar.api.identity.AuthorInfo.Status
import org.briarproject.briar.desktop.ui.ListItemView
import org.briarproject.briar.desktop.ui.TrustIndicatorShort
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18nF
import org.briarproject.briar.desktop.utils.PreviewUtils.DropDownValues
import org.briarproject.briar.desktop.utils.PreviewUtils.preview
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
    "selected" to false,
) {
    Column(Modifier.selectableGroup()) {
        ListItemView(getBooleanParameter("selected")) {
            ContactItemViewSmall(
                ContactItem(
                    id = ContactId(0),
                    authorId = AuthorId(getRandomIdPersistent()),
                    authorInfo = getRandomAuthorInfo(Status.valueOf(getStringParameter("trustLevel"))),
                    name = getStringParameter("name"),
                    alias = getStringParameter("alias"),
                    isConnected = getBooleanParameter("isConnected"),
                    isEmpty = getBooleanParameter("isEmpty"),
                    unread = getIntParameter("unread"),
                    timestamp = getLongParameter("timestamp"),
                ),
            )
        }
    }
}

@Composable
fun ContactItemViewSmall(
    contactItem: ContactItem,
    showConnectionState: Boolean = true,
    modifier: Modifier = Modifier,
) = Row(
    horizontalArrangement = spacedBy(8.dp),
    verticalAlignment = Alignment.CenterVertically,
    modifier = modifier
        .fillMaxWidth()
        .semantics {
            text = getDescription(contactItem, showConnectionState)
        }
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = spacedBy(8.dp),
        modifier = Modifier.weight(1f, fill = true),
    ) {
        // TODO cache profile images, if available
        ProfileCircle(20.dp, contactItem)
        Text(
            text = contactItem.displayName,
            style = MaterialTheme.typography.body1,
            maxLines = 3,
            overflow = Ellipsis,
        )
        TrustIndicatorShort(contactItem.trustLevel)
    }
    if (showConnectionState)
        ConnectionIndicator(
            modifier = Modifier.requiredSize(12.dp),
            isConnected = contactItem.isConnected
        )
}

private fun getDescription(
    contactItem: ContactItem,
    showConnectionState: Boolean,
) = buildBlankAnnotatedString {
    append(i18nF("access.contact.with_name", contactItem.displayName))
    // todo: trust level!
    if (showConnectionState)
        appendCommaSeparated(
            if (contactItem.isConnected) i18n("access.contact.connected.yes")
            else i18n("access.contact.connected.no")
        )
    append('.')
}
