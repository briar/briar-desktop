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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Row
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.briarproject.bramble.api.identity.Author
import org.briarproject.briar.api.identity.AuthorInfo
import org.briarproject.briar.api.identity.AuthorInfo.Status.OURSELVES
import org.briarproject.briar.desktop.contact.ProfileCircle
import org.briarproject.briar.desktop.utils.TimeUtils
import org.briarproject.briar.desktop.utils.UiUtils.getContactDisplayName
import org.briarproject.briar.desktop.utils.UiUtils.modifyIf

@Composable
fun AuthorView(
    author: Author,
    authorInfo: AuthorInfo,
    timestamp: Long,
    modifier: Modifier = Modifier,
    avatarSize: Dp = 27.dp,
    onAuthorClicked: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = spacedBy(8.dp),
        verticalAlignment = CenterVertically,
    ) {
        Row(
            modifier = Modifier.weight(1f)
                .modifyIf(onAuthorClicked != null, Modifier.clickable { onAuthorClicked?.invoke() }),
            horizontalArrangement = spacedBy(8.dp),
            verticalAlignment = CenterVertically,
        ) {
            ProfileCircle(avatarSize, author.id, authorInfo)
            Text(
                modifier = Modifier.weight(1f, fill = false),
                text = getContactDisplayName(author.name, authorInfo.alias),
                fontWeight = if (authorInfo.status == OURSELVES) Bold else null,
                maxLines = 1,
                overflow = Ellipsis,
            )
            TrustIndicatorShort(authorInfo.status)
        }
        Text(
            text = TimeUtils.getFormattedTimestamp(timestamp),
            textAlign = TextAlign.End,
            style = MaterialTheme.typography.caption,
            maxLines = 1,
        )
    }
}
