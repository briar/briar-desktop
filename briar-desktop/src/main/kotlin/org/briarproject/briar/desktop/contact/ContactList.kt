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

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import org.briarproject.briar.desktop.theme.surfaceVariant
import org.briarproject.briar.desktop.ui.Constants.COLUMN_WIDTH
import org.briarproject.briar.desktop.ui.Constants.HEADER_SIZE
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n

@Composable
fun ContactList(
    contactList: List<BaseContactItem>,
    isSelected: (BaseContactItem) -> Boolean,
    selectContact: (BaseContactItem) -> Unit,
    removePendingContact: (PendingContactItem) -> Unit,
    filterBy: String,
    setFilterBy: (String) -> Unit,
    onContactAdd: () -> Unit,
) {
    val scrollState = rememberLazyListState()

    Surface(
        modifier = Modifier.fillMaxHeight().width(COLUMN_WIDTH),
        color = MaterialTheme.colors.surfaceVariant
    ) {
        Column {
            Column(
                modifier = Modifier.fillMaxWidth().height(HEADER_SIZE + 1.dp),
            ) {
                SearchTextField(
                    placeholder = i18n("contacts.search.title"),
                    icon = Icons.Filled.PersonAdd,
                    searchValue = filterBy,
                    onValueChange = setFilterBy,
                    onAddButtonClicked = onContactAdd,
                )
            }

            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    state = scrollState,
                    modifier = Modifier
                        .semantics {
                            contentDescription = i18n("access.contact.list")
                        }
                        .selectableGroup()
                ) {
                    items(contactList) { contactItem ->
                        ContactCard(
                            contactItem,
                            onSel = { selectContact(contactItem) },
                            selected = isSelected(contactItem),
                            onRemovePending = { if (contactItem is PendingContactItem) removePendingContact(contactItem) },
                        )
                    }
                }

                VerticalScrollbar(
                    adapter = rememberScrollbarAdapter(scrollState),
                    modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight()
                )
            }
        }
    }
}
