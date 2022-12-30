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

package org.briarproject.briar.desktop.forums.sharing

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.briarproject.briar.api.sharing.SharingManager.SharingStatus.ERROR
import org.briarproject.briar.api.sharing.SharingManager.SharingStatus.INVITE_RECEIVED
import org.briarproject.briar.api.sharing.SharingManager.SharingStatus.INVITE_SENT
import org.briarproject.briar.api.sharing.SharingManager.SharingStatus.NOT_SUPPORTED
import org.briarproject.briar.api.sharing.SharingManager.SharingStatus.SHAREABLE
import org.briarproject.briar.api.sharing.SharingManager.SharingStatus.SHARING
import org.briarproject.briar.desktop.contact.ContactItemViewSmall
import org.briarproject.briar.desktop.ui.Constants.HEADER_SIZE
import org.briarproject.briar.desktop.ui.HorizontalDivider
import org.briarproject.briar.desktop.ui.ListItemView
import org.briarproject.briar.desktop.ui.VerticallyScrollableArea
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n

@Composable
fun ForumSharingActionDrawerContent(
    close: () -> Unit,
    viewModel: ForumSharingViewModel,
) = Column {
    Row(Modifier.fillMaxWidth().height(HEADER_SIZE)) {
        IconButton(
            icon = Icons.Filled.Close,
            contentDescription = i18n("access.forum.sharing.action.close"),
            onClick = close,
            modifier = Modifier.padding(start = 24.dp).size(24.dp).align(Alignment.CenterVertically)
        )
        Text(
            text = i18n("forum.sharing.action.title"),
            modifier = Modifier.align(Alignment.CenterVertically).padding(start = 16.dp),
            style = MaterialTheme.typography.h3,
        )
    }
    HorizontalDivider()
    Box(Modifier.fillMaxWidth().weight(1f)) {
        if (viewModel.contactList.value.isEmpty()) {
            // todo: this might be shown to the user while the list is still loading
            Text(
                text = i18n("forum.sharing.action.no_contacts"),
                style = MaterialTheme.typography.body1,
                modifier = Modifier.padding(8.dp).align(Alignment.Center),
            )
        } else {
            VerticallyScrollableArea { scrollState ->
                LazyColumn(state = scrollState) {
                    items(
                        items = viewModel.contactList.value,
                        key = { it.contactItem.id },
                    ) { shareableContactItem ->
                        ForumSharingActionListItem(
                            shareableContactItem = shareableContactItem,
                            shareable = shareableContactItem.status == SHAREABLE,
                            selected = viewModel.isShareableSelected(shareableContactItem),
                            onToggle = { viewModel.toggleShareable(shareableContactItem) },
                        )
                    }
                }
            }
        }
    }
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth().padding(8.dp),
    ) {
        val shareForum = {
            if (viewModel.buttonEnabled.value) {
                viewModel.shareForum()
                close()
            }
        }

        TextField(
            value = viewModel.sharingMessage.value,
            onValueChange = viewModel::setSharingMessage,
            onEnter = shareForum,
            placeholder = {
                Text(
                    text = i18n("forum.sharing.action.add_message"),
                    style = MaterialTheme.typography.body1,
                )
            },
            modifier = Modifier.fillMaxWidth().heightIn(max = 100.dp)
        )
        Button(
            onClick = shareForum,
            modifier = Modifier.fillMaxWidth(),
            enabled = viewModel.buttonEnabled.value,
        ) {
            Text(i18n("forum.sharing.action.title"))
        }
    }
}

@Composable
private fun ForumSharingActionListItem(
    shareableContactItem: ForumSharingViewModel.ShareableContactItem,
    shareable: Boolean,
    selected: Boolean,
    onToggle: () -> Unit,
) = ListItemView(
    selected = if (shareable) selected else null,
    onSelect = onToggle,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            checked = selected,
            onCheckedChange = { onToggle() },
            enabled = shareable
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(8.dp)
        ) {
            ContactItemViewSmall(
                shareableContactItem.contactItem,
                showConnectionState = false,
            )
            if (!shareable) {
                Text(
                    when (shareableContactItem.status) {
                        SHAREABLE -> ""
                        SHARING -> i18n("forum.sharing.action.status.already_shared")
                        INVITE_SENT -> i18n("forum.sharing.action.status.already_invited")
                        INVITE_RECEIVED -> i18n("forum.sharing.action.status.invite_received")
                        NOT_SUPPORTED -> i18n("forum.sharing.action.status.not_supported")
                        ERROR -> i18n("forum.sharing.action.status.error")
                    },
                    style = MaterialTheme.typography.caption,
                )
            }
        }
    }
}
