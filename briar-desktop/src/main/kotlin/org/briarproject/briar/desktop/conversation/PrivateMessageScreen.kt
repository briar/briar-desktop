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

package org.briarproject.briar.desktop.conversation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.briarproject.briar.desktop.contact.ConfirmRemovePendingContactDialog
import org.briarproject.briar.desktop.contact.ContactItem
import org.briarproject.briar.desktop.contact.ContactList
import org.briarproject.briar.desktop.contact.ContactListViewModel
import org.briarproject.briar.desktop.contact.add.remote.AddContactDialog
import org.briarproject.briar.desktop.contact.add.remote.AddContactViewModel
import org.briarproject.briar.desktop.contact.add.remote.PendingContactItem
import org.briarproject.briar.desktop.ui.BriarLogo
import org.briarproject.briar.desktop.ui.ColoredIconButton
import org.briarproject.briar.desktop.ui.Constants.PARAGRAPH_WIDTH
import org.briarproject.briar.desktop.ui.VerticalDivider
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n
import org.briarproject.briar.desktop.viewmodel.viewModel

@Composable
fun PrivateMessageScreen(
    viewModel: ContactListViewModel = viewModel(),
    addContactViewModel: AddContactViewModel = viewModel(),
) {
    AddContactDialog(addContactViewModel)

    ConfirmRemovePendingContactDialog(
        viewModel.removePendingContactDialogVisible.value,
        close = viewModel::dismissRemovePendingContactDialog,
        onRemove = viewModel::confirmRemovingPendingContact,
    )

    if (viewModel.noContactsYet.value) {
        NoContactsYet(onContactAdd = { addContactViewModel.showDialog() })
        return
    }

    Row(modifier = Modifier.fillMaxWidth()) {
        ContactList(
            viewModel.combinedContactList.value,
            viewModel::isSelected,
            viewModel::selectContact,
            viewModel::removePendingContact,
            viewModel.filterBy.value,
            viewModel::setFilterBy,
            onContactAdd = { addContactViewModel.showDialog() }
        )
        VerticalDivider()
        Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
            val item = viewModel.selectedContactListItem.value
            if (item == null) {
                NoContactSelected()
            } else when (item) {
                is ContactItem -> {
                    ConversationScreen(item.id)
                }
                is PendingContactItem -> {
                    PendingContactSelected()
                }
            }
        }
    }
}

@Composable
fun NoContactsYet(onContactAdd: () -> Unit) = Explainer(
    headline = i18n("welcome.title"),
    text = i18n("welcome.text"),
) {
    ColoredIconButton(
        icon = Icons.Filled.PersonAdd,
        iconSize = 20.dp,
        contentDescription = i18n("access.contacts.add"),
        onClick = onContactAdd,
    )
}

@Composable
fun NoContactSelected() = Explainer(
    headline = i18n("contacts.none_selected.title"),
    text = i18n("contacts.none_selected.hint"),
)

@Composable
fun PendingContactSelected() = Explainer(
    headline = i18n("contacts.pending_selected.title"),
    text = i18n("contacts.pending_selected.hint"),
)

@Composable
fun Explainer(headline: String, text: String, content: @Composable () -> Unit = {}) =
    Column(
        modifier = Modifier.padding(16.dp).fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BriarLogo(modifier = Modifier.size(200.dp))
        Text(
            text = headline,
            modifier = Modifier.padding(top = 16.dp, bottom = 4.dp),
            style = MaterialTheme.typography.h3
        )
        Text(
            text = text,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp).widthIn(max = PARAGRAPH_WIDTH),
            style = MaterialTheme.typography.body2.copy(textAlign = TextAlign.Center)
        )
        content()
    }
