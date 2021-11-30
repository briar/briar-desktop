package org.briarproject.briar.desktop.privategroups

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import org.briarproject.bramble.api.sync.GroupId
import org.briarproject.briar.desktop.theme.surfaceVariant
import org.briarproject.briar.desktop.ui.Constants.PRIVATE_GROUP_LIST_WIDTH

@Composable
fun PrivateGroupList(
    privateGroupList: List<PrivateGroupItem>,
    isSelected: (GroupId) -> Boolean,
    selectPrivateGroup: (GroupId) -> Unit,
) {
    // TODO AddPrivateGroupDialog
    Scaffold(
        modifier = Modifier.fillMaxHeight().width(PRIVATE_GROUP_LIST_WIDTH),
        backgroundColor = MaterialTheme.colors.surfaceVariant,
        // TODO SearchTextField
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
