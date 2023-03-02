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

package org.briarproject.briar.desktop.introduction

import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
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
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.briarproject.briar.desktop.contact.ContactItem
import org.briarproject.briar.desktop.contact.ContactItemViewSmall
import org.briarproject.briar.desktop.ui.Constants.HEADER_SIZE
import org.briarproject.briar.desktop.ui.HorizontalDivider
import org.briarproject.briar.desktop.ui.ListItemView
import org.briarproject.briar.desktop.ui.VerticallyScrollableArea
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18nF
import org.briarproject.briar.desktop.viewmodel.viewModel

@Composable
fun IntroductionDrawerContent(
    contactItem: ContactItem,
    close: (reload: Boolean) -> Unit,
    viewModel: IntroductionViewModel = viewModel(),
) {
    LaunchedEffect(contactItem) {
        viewModel.setFirstContact(contactItem)
    }
    Column {
        Row(
            verticalAlignment = CenterVertically,
            modifier = Modifier.fillMaxWidth().height(HEADER_SIZE),
        ) {
            IconButton(
                icon = Icons.Filled.Close,
                contentDescription = i18n("access.introduction.close"),
                onClick = { close(false) },
                modifier = Modifier.padding(start = 24.dp).size(24.dp)
            )
            Text(
                text = i18n("introduction.introduce"),
                modifier = Modifier.padding(start = 16.dp),
                style = MaterialTheme.typography.h3,
            )
        }
        HorizontalDivider()
        Text(
            text = i18nF("introduction.title", contactItem.displayName) + ":",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            style = MaterialTheme.typography.body2,
        )
        HorizontalDivider()
        Box(Modifier.fillMaxWidth().weight(1f)) {
            if (viewModel.contactList.value.isEmpty()) {
                // todo: this might be shown to the user while the list is still loading
                EmptyList()
            } else {
                List(viewModel)
            }
        }
        Column(
            verticalArrangement = spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth().padding(8.dp),
        ) {
            val makeIntroduction = {
                if (viewModel.buttonEnabled.value) {
                    viewModel.makeIntroduction()
                    close(true)
                }
            }

            TextField(
                value = viewModel.introductionMessage.value,
                onValueChange = viewModel::setIntroductionMessage,
                onEnter = makeIntroduction,
                placeholder = {
                    Text(
                        text = i18n("introduction.message"),
                        style = MaterialTheme.typography.body1,
                    )
                },
                modifier = Modifier.fillMaxWidth().heightIn(max = 100.dp)
            )
            Button(
                onClick = makeIntroduction,
                modifier = Modifier.fillMaxWidth(),
                enabled = viewModel.buttonEnabled.value,
            ) {
                Text(i18n("introduction.introduce"))
            }
        }
    }
}

@Composable
private fun BoxScope.EmptyList() = Text(
    text = i18n("introduction.state.no_contacts"),
    style = MaterialTheme.typography.body1,
    modifier = Modifier.padding(8.dp).align(Center),
)

@Composable
private fun List(viewModel: IntroductionViewModel) = VerticallyScrollableArea { scrollState ->
    LazyColumn(state = scrollState) {
        items(
            items = viewModel.contactList.value,
            key = { it.contactItem.id },
        ) { introductionContactItem ->
            IntroductionListItemView(
                introductionContactItem = introductionContactItem,
                selected = viewModel.isSecondContactSelected(introductionContactItem),
                onToggle = { viewModel.toggleSecondContact(introductionContactItem) },
            )
        }
    }
}

@Composable
private fun IntroductionListItemView(
    introductionContactItem: IntroductionViewModel.IntroductionContactItem,
    selected: Boolean,
    onToggle: () -> Unit,
) = ListItemView(
    selected = if (introductionContactItem.introductionPossible) selected else null,
    onSelect = onToggle,
    multiSelectWithCheckbox = true,
) {
    Column(
        verticalArrangement = spacedBy(4.dp),
        modifier = Modifier.padding(vertical = 8.dp).padding(end = 8.dp)
    ) {
        ContactItemViewSmall(
            introductionContactItem.contactItem,
            showConnectionState = false,
        )
        if (!introductionContactItem.introductionPossible) {
            Text(
                text = i18n("introduction.state.introduction_ongoing"),
                style = MaterialTheme.typography.caption,
            )
        }
    }
}
