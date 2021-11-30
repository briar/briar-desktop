package org.briarproject.briar.desktop.privategroups

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.briarproject.briar.desktop.ui.UiPlaceholder
import org.briarproject.briar.desktop.ui.VerticalDivider
import org.briarproject.briar.desktop.viewmodel.viewModel

@Composable
fun PrivateGroupScreen(
    viewModel: PrivateGroupListViewModel = viewModel(),
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        PrivateGroupList(
            viewModel.privateGroupList,
            viewModel::isSelected,
            viewModel::selectPrivateGroup,
        )
        VerticalDivider()
        Column(modifier = Modifier.weight(1f).fillMaxHeight()) {
            val id = viewModel.selectedPrivateGroupId.value
            if (id != null) {
                ThreadedConversationScreen(id)
            } else {
                UiPlaceholder()
            }
        }
    }
}
