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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.briarproject.briar.desktop.contact.ContactList
import org.briarproject.briar.desktop.contact.ContactListViewModel
import org.briarproject.briar.desktop.contact.PendingContactIdWrapper
import org.briarproject.briar.desktop.contact.RealContactIdWrapper
import org.briarproject.briar.desktop.ui.BriarLogo
import org.briarproject.briar.desktop.ui.Constants.PARAGRAPH_WIDTH
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
fun NoContactSelected() = Explainer(
    headline = i18n("contacts.none_selected.title"),
    content = i18n("contacts.none_selected.hint"),
)

@Composable
fun PendingContactSelected() = Explainer(
    headline = i18n("contacts.pending_selected.title"),
    content = i18n("contacts.pending_selected.hint"),
)

@Composable
fun Explainer(headline: String, content: String) = Surface(color = MaterialTheme.colors.background) {
    Column(
        modifier = Modifier.padding(16.dp).fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BriarLogo(modifier = Modifier.size(200.dp))
        Text(
            text = headline,
            modifier = Modifier.padding(PaddingValues(top = 15.dp, bottom = 5.dp)),
            style = MaterialTheme.typography.h5
        )
        Text(
            text = content,
            modifier = Modifier.padding(PaddingValues(top = 5.dp, bottom = 15.dp)).widthIn(max = PARAGRAPH_WIDTH),
            style = MaterialTheme.typography.body2.copy(textAlign = TextAlign.Center)
        )
    }
}
