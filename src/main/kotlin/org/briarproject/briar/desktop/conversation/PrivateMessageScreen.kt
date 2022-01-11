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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.briarproject.briar.desktop.contact.ContactList
import org.briarproject.briar.desktop.contact.ContactListViewModel
import org.briarproject.briar.desktop.contact.PendingContactIdWrapper
import org.briarproject.briar.desktop.contact.RealContactIdWrapper
import org.briarproject.briar.desktop.contact.add.remote.AddContactDialog
import org.briarproject.briar.desktop.ui.BriarLogo
import org.briarproject.briar.desktop.ui.Constants.PARAGRAPH_WIDTH
import org.briarproject.briar.desktop.ui.VerticalDivider
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n
import org.briarproject.briar.desktop.viewmodel.viewModel

@Composable
fun PrivateMessageScreen(
    viewModel: ContactListViewModel = viewModel(),
) {
    var isContactDialogVisible by remember { mutableStateOf(false) }
    if (isContactDialogVisible) AddContactDialog(onClose = { isContactDialogVisible = false })

    if (viewModel.noContactsYet.value) {
        NoContactsYet(onContactAdd = { isContactDialogVisible = true })
        return
    }

    Row(modifier = Modifier.fillMaxWidth()) {
        ContactList(
            viewModel.contactList.value,
            viewModel::isSelected,
            viewModel::selectContact,
            viewModel.filterBy.value,
            viewModel::setFilterBy,
            onContactAdd = { isContactDialogVisible = true }
        )
        VerticalDivider()
        Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
            val id = viewModel.selectedContactId.value
            if (id == null) {
                NoContactSelected()
            } else when (id) {
                is RealContactIdWrapper -> {
                    ConversationScreen(id.contactId)
                }
                is PendingContactIdWrapper -> {
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
    IconButton(
        onClick = onContactAdd,
        modifier = Modifier.padding(end = 10.dp).size(32.dp)
            .background(MaterialTheme.colors.primary, CircleShape)
    ) {
        Icon(
            Icons.Filled.PersonAdd,
            i18n("access.contacts.add"),
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )
    }
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
    Surface(color = MaterialTheme.colors.background) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BriarLogo(modifier = Modifier.size(200.dp))
            Text(
                text = headline,
                modifier = Modifier.padding(top = 15.dp, bottom = 5.dp),
                style = MaterialTheme.typography.h5
            )
            Text(
                text = text,
                modifier = Modifier.padding(top = 5.dp, bottom = 15.dp).widthIn(max = PARAGRAPH_WIDTH),
                style = MaterialTheme.typography.body2.copy(textAlign = TextAlign.Center)
            )
            content()
        }
    }
