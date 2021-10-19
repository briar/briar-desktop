package org.briarproject.briar.desktop.contact

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.briarproject.briar.desktop.contact.add.remote.AddContactDialog
import org.briarproject.briar.desktop.contact.add.remote.AddContactViewModel
import org.briarproject.briar.desktop.theme.surfaceVariant
import org.briarproject.briar.desktop.ui.Constants.CONTACTLIST_WIDTH
import org.briarproject.briar.desktop.ui.Constants.HEADER_SIZE

@Composable
fun ContactList(
    viewModel: ContactListViewModel,
    addContactViewModel: AddContactViewModel,
) {
    var isContactDialogVisible by remember { mutableStateOf(false) }
    if (isContactDialogVisible) AddContactDialog(addContactViewModel) { isContactDialogVisible = false }
    Scaffold(
        modifier = Modifier.fillMaxHeight().width(CONTACTLIST_WIDTH),
        backgroundColor = MaterialTheme.colors.surfaceVariant,
        topBar = {
            Column(
                modifier = Modifier.fillMaxWidth().height(HEADER_SIZE + 1.dp),
            ) {
                SearchTextField(
                    viewModel.filterBy.value,
                    onValueChange = viewModel::setFilterBy,
                    onContactAdd = { isContactDialogVisible = true }
                )
            }
        },
        content = {
            LazyColumn {
                itemsIndexed(viewModel.contactList) { index, contactItem ->
                    ContactCard(contactItem, { viewModel.selectContact(index) }, viewModel.isSelected(index))
                }
            }
        },
    )
}
