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

package org.briarproject.briar.desktop.privategroup.sharing

import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.briarproject.briar.api.identity.AuthorInfo.Status.OURSELVES
import org.briarproject.briar.api.privategroup.GroupMember
import org.briarproject.briar.desktop.contact.ContactItemViewSmall
import org.briarproject.briar.desktop.ui.Constants.HEADER_SIZE
import org.briarproject.briar.desktop.ui.HorizontalDivider
import org.briarproject.briar.desktop.ui.ListItemView
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18nF
import org.briarproject.briar.desktop.utils.UiUtils.getContactDisplayName

@Composable
fun PrivateGroupMemberDrawerContent(
    close: () -> Unit,
    viewModel: PrivateGroupSharingViewModel,
) = Column {
    Row(Modifier.fillMaxWidth().height(HEADER_SIZE)) {
        IconButton(
            icon = Icons.Filled.Close,
            contentDescription = i18n("access.group.member.close"),
            onClick = close,
            modifier = Modifier.padding(start = 24.dp).size(24.dp).align(CenterVertically)
        )
        Text(
            text = i18n("group.member.title"),
            modifier = Modifier.align(CenterVertically).padding(start = 16.dp),
            style = MaterialTheme.typography.h3,
        )
    }
    HorizontalDivider()
    Row(
        verticalAlignment = CenterVertically,
        horizontalArrangement = spacedBy(8.dp),
        modifier = Modifier.padding(8.dp),
    ) {
        Icon(
            imageVector = Icons.Filled.Info,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = i18n("group.member.info"),
            style = MaterialTheme.typography.body2,
        )
    }
    HorizontalDivider()
    LazyColumn(Modifier.fillMaxSize()) {
        items(viewModel.members) { groupMember ->
            PrivateGroupMemberListItem(groupMember)
        }
    }
}

@Composable
private fun PrivateGroupMemberListItem(
    groupMember: GroupMember,
) = ListItemView {
    Column(
        verticalArrangement = spacedBy(8.dp),
        modifier = Modifier.padding(8.dp)
    ) {
        ContactItemViewSmall(
            displayName = groupMember.author.name,
            authorId = groupMember.author.id,
            authorInfo = groupMember.authorInfo,
            // todo: Android shows connection status if contact
            isConnected = null,
        )
        if (groupMember.isCreator) {
            Text(
                if (groupMember.authorInfo.status == OURSELVES) i18n("group.member.created_you")
                else i18nF(
                    "group.member.created_contact",
                    getContactDisplayName(groupMember.author.name, groupMember.authorInfo.alias)
                ),
                style = MaterialTheme.typography.caption,
            )
        }
    }
}
