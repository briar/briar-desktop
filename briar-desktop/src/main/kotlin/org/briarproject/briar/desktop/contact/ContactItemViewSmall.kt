/*
 * Briar Desktop
 * Copyright (C) 2021-2023 The Briar Project
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredSize
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
import org.briarproject.briar.api.identity.AuthorInfo
import org.briarproject.briar.api.identity.AuthorInfo.Status
import org.briarproject.briar.desktop.privategroup.sharing.GroupMemberItem
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
    "showConnectionState" to false,
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
    ContactItemViewSmall(item, getBooleanParameter("showConnectionState"))
}

@Composable
fun ContactItemViewSmall(
    contactItem: ContactItem,
    showConnectionState: Boolean = true,
    modifier: Modifier = Modifier,
) = ContactItemViewSmall(
    displayName = contactItem.displayName,
    authorId = contactItem.authorId,
    authorInfo = contactItem.authorInfo,
    isConnected = if (showConnectionState) contactItem.isConnected else null,
    modifier = modifier,
)

@Composable
fun ContactItemViewSmall(
    groupMemberItem: GroupMemberItem,
    modifier: Modifier = Modifier,
) = ContactItemViewSmall(
    displayName = groupMemberItem.displayName,
    authorId = groupMemberItem.authorId,
    authorInfo = groupMemberItem.authorInfo,
    isConnected = groupMemberItem.isConnected,
    modifier = modifier,
)

@Composable
fun ContactItemViewSmall(
    displayName: String,
    authorId: AuthorId,
    authorInfo: AuthorInfo,
    isConnected: Boolean? = null,
    modifier: Modifier = Modifier,
) = Row(
    horizontalArrangement = spacedBy(8.dp),
    verticalAlignment = Alignment.CenterVertically,
    modifier = modifier
        .fillMaxWidth()
        .semantics {
            text = getDescription(displayName, isConnected)
        }
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = spacedBy(8.dp),
        modifier = Modifier.weight(1f, fill = true),
    ) {
        ProfileCircle(27.dp, authorId, authorInfo)
        Text(
            modifier = Modifier.weight(1f, fill = false),
            text = displayName,
            style = MaterialTheme.typography.body1,
            maxLines = 3,
            overflow = Ellipsis,
        )
        TrustIndicatorShort(authorInfo.status)
    }
    if (isConnected != null)
        ConnectionIndicator(
            modifier = Modifier.requiredSize(12.dp),
            isConnected = isConnected
        )
}

private fun getDescription(
    displayName: String,
    isConnected: Boolean?,
) = buildBlankAnnotatedString {
    append(i18nF("access.contact.with_name", displayName))
    // todo: trust level!
    if (isConnected != null)
        appendCommaSeparated(
            if (isConnected) i18n("access.contact.connected.yes")
            else i18n("access.contact.connected.no")
        )
    append('.')
}
