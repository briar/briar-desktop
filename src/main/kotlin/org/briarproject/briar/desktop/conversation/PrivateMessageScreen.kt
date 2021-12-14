package org.briarproject.briar.desktop.conversation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.briarproject.briar.desktop.contact.ContactList
import org.briarproject.briar.desktop.contact.ContactListViewModel
import org.briarproject.briar.desktop.contact.RealContactIdWrapper
import org.briarproject.briar.desktop.ui.UiPlaceholder
import org.briarproject.briar.desktop.ui.VerticalDivider
import org.briarproject.briar.desktop.viewmodel.viewModel

@Composable
fun PrivateMessageScreen(
    viewModel: ContactListViewModel = viewModel(),
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        ContactList(
            viewModel.contactList,
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
                UiPlaceholder()
            }
        }
    }
}
