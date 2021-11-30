package org.briarproject.briar.desktop.privategroups

import androidx.compose.runtime.Composable
import org.briarproject.bramble.api.sync.GroupId
import org.briarproject.briar.desktop.ui.UiPlaceholder
import org.briarproject.briar.desktop.viewmodel.viewModel

@Composable
fun ThreadedConversationScreen(
    groupId: GroupId,
    viewModel: ThreadedConversationViewModel = viewModel(),
) = UiPlaceholder()
