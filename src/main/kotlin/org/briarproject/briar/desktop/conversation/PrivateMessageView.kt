package org.briarproject.briar.desktop.conversation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.briarproject.briar.desktop.contact.ContactList
import org.briarproject.briar.desktop.contact.ContactListViewModel
import org.briarproject.briar.desktop.contact.add.remote.AddContactViewModel
import org.briarproject.briar.desktop.introduction.IntroductionViewModel
import org.briarproject.briar.desktop.ui.UiPlaceholder
import org.briarproject.briar.desktop.ui.VerticalDivider

@Composable
fun PrivateMessageView(
    contactListViewModel: ContactListViewModel,
    addContactViewModel: AddContactViewModel,
    introductionViewModel: IntroductionViewModel,
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        ContactList(contactListViewModel, addContactViewModel)
        VerticalDivider()
        Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
            contactListViewModel.selectedContact.value?.also { selectedContact ->
                Conversation(
                    selectedContact,
                    introductionViewModel
                )
            } ?: UiPlaceholder()
        }
    }
}
