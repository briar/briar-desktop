package org.briarproject.briar.desktop.contact

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.briarproject.briar.desktop.contact.add.remote.AddContactDialog
import org.briarproject.briar.desktop.theme.surfaceVariant
import org.briarproject.briar.desktop.ui.Constants.CONTACTLIST_WIDTH
import org.briarproject.briar.desktop.ui.Constants.HEADER_SIZE

@Composable
fun ContactList(
    contactList: List<BaseContactItem>,
    isSelected: (BaseContactItem) -> Boolean,
    selectContact: (BaseContactItem) -> Unit,
    filterBy: String,
    setFilterBy: (String) -> Unit,
) {
    var isContactDialogVisible by remember { mutableStateOf(false) }
    if (isContactDialogVisible) AddContactDialog(onClose = { isContactDialogVisible = false })

    val scrollState = rememberLazyListState()

    Scaffold(
        modifier = Modifier.fillMaxHeight().width(CONTACTLIST_WIDTH),
        backgroundColor = MaterialTheme.colors.surfaceVariant,
        topBar = {
            Column(
                modifier = Modifier.fillMaxWidth().height(HEADER_SIZE + 1.dp),
            ) {
                SearchTextField(
                    filterBy,
                    onValueChange = setFilterBy,
                    onContactAdd = { isContactDialogVisible = true }
                )
            }
        },
        content = { padding ->
            Box(modifier = Modifier.padding(padding).fillMaxSize()) {
                LazyColumn(state = scrollState) {
                    items(contactList) { contactItem ->
                        ContactCard(
                            contactItem,
                            { selectContact(contactItem) },
                            isSelected(contactItem),
                            PaddingValues(end = 12.dp)
                        )
                    }
                }

                VerticalScrollbar(
                    adapter = rememberScrollbarAdapter(scrollState),
                    modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight()
                )
            }
        },
    )
}
