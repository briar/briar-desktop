package org.briarproject.briar.desktop.privategroups

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.briarproject.bramble.api.sync.GroupId
import org.briarproject.briar.desktop.contact.SearchTextField
import org.briarproject.briar.desktop.contact.add.remote.AddContactDialog
import org.briarproject.briar.desktop.theme.surfaceVariant
import org.briarproject.briar.desktop.ui.Constants.HEADER_SIZE
import org.briarproject.briar.desktop.ui.Constants.PRIVATE_GROUP_LIST_WIDTH

@Composable
fun PrivateGroupList(
    privateGroupList: List<PrivateGroupItem>,
    isSelected: (GroupId) -> Boolean,
    selectPrivateGroup: (GroupId) -> Unit,
    filterBy: String,
    setFilterBy: (String) -> Unit,
) {
    var isCreatePrivateGroupDialogVisible by remember { mutableStateOf(false) }
    if (isCreatePrivateGroupDialogVisible) AddContactDialog(onClose = { isCreatePrivateGroupDialogVisible = false })
    Scaffold(
        modifier = Modifier.fillMaxHeight().width(PRIVATE_GROUP_LIST_WIDTH),
        backgroundColor = MaterialTheme.colors.surfaceVariant,
        topBar = {
            Column(
                modifier = Modifier.fillMaxWidth().height(HEADER_SIZE + 1.dp),
            ) {
                SearchTextField(
                    filterBy,
                    onValueChange = setFilterBy,
                    onContactAdd = { isCreatePrivateGroupDialogVisible = true }
                )
            }
        },
        content = {
            LazyColumn {
                items(privateGroupList) { privateGroupItem ->
                    PrivateGroupCard(
                        privateGroupItem,
                        { selectPrivateGroup(privateGroupItem.privateGroup.id) },
                        isSelected(privateGroupItem.privateGroup.id)
                    )
                }
            }
        },
    )
}