package org.briarproject.briar.desktop.conversation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.briarproject.briar.desktop.contact.ContactList
import org.briarproject.briar.desktop.contact.ContactListViewModel
import org.briarproject.briar.desktop.contact.RealContactIdWrapper
import org.briarproject.briar.desktop.ui.BriarLogo
import org.briarproject.briar.desktop.ui.VerticalDivider
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n
import org.briarproject.briar.desktop.viewmodel.viewModel

@Composable
fun PrivateMessageScreen(
    viewModel: ContactListViewModel = viewModel(),
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        ContactList(
            viewModel.contactList.value,
            viewModel::isSelected,
            viewModel::selectContact,
            viewModel.filterBy.value,
            viewModel::setFilterBy
        )
        VerticalDivider()
        Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
            val id = viewModel.selectedContactId.value
            if (id != null && id is RealContactIdWrapper) {
                ConversationScreen(id.contactId)
            } else {
                NoContactSelected()
            }
        }
    }
}

@Composable
fun NoContactSelected() = Surface(color = MaterialTheme.colors.background) {
    Column(
        modifier = Modifier.padding(16.dp).fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BriarLogo(modifier = Modifier.size(200.dp))
        Text(
            text = i18n("contacts.none_selected.title"),
            modifier = Modifier.padding(PaddingValues(top = 15.dp, bottom = 5.dp)),
            style = MaterialTheme.typography.h5
        )
        Text(
            text = i18n("contacts.none_selected.hint"),
            modifier = Modifier.padding(PaddingValues(top = 5.dp, bottom = 15.dp)),
            style = MaterialTheme.typography.body2
        )
    }
}
