package org.briarproject.briar.desktop.privategroups

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.briarproject.bramble.api.sync.GroupId
import org.briarproject.briar.desktop.contact.PreviewProfileCircle
import org.briarproject.briar.desktop.contact.ProfileCircle
import org.briarproject.briar.desktop.conversation.ConversationInput
import org.briarproject.briar.desktop.theme.divider
import org.briarproject.briar.desktop.ui.Constants
import org.briarproject.briar.desktop.ui.HorizontalDivider
import org.briarproject.briar.desktop.ui.Loader
import org.briarproject.briar.desktop.utils.InternationalizationUtils
import org.briarproject.briar.desktop.viewmodel.viewModel

@Composable
fun SingleThreadScreen(
    groupId: GroupId,
    viewModel: ThreadedConversationViewModel = viewModel(),
    singleThreadViewModel: SingleThreadViewModel = viewModel(),
    ) {
    val contactItem = viewModel.contactItem.value

    if (contactItem == null) {
        Loader()
        return
    }
    Scaffold(
        topBar = {
                 SingleThreadHeader(singleThreadViewModel)
        },
        content = { padding ->
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                // reverseLayout to display most recent message (index 0) at the bottom
                contentPadding = PaddingValues(8.dp),
                modifier = Modifier.padding(padding).fillMaxHeight()
            ) {
                item {
                    SingleThreadMessage()
                }
                item {
                    SingleThreadMessage()
                }
                item {
                    SingleThreadMessage()
                }
                item {
                    SingleThreadMessage()
                }
                item {
                    SingleThreadMessage()
                }
            }
        },
        bottomBar = {
            ConversationInput(
                viewModel.newMessage.value,
                viewModel::setNewMessage,
                viewModel::sendMessage
            )
        },
    )
}

@Composable
fun SingleThreadHeader(singleThreadViewModel: SingleThreadViewModel) {
    Box(modifier = Modifier.fillMaxWidth().height(Constants.HEADER_SIZE + 1.dp)) {
        IconButton(
            onClick = { singleThreadViewModel.setViewState(false) },
            modifier = Modifier.align(Alignment.CenterStart).padding(end = 16.dp)
        ) {
            Icon(Icons.Filled.ArrowBack, InternationalizationUtils.i18n("access.contact.menu"), modifier = Modifier.size(24.dp))
        }
        Row(modifier = Modifier.align(Alignment.Center)) {
            PreviewProfileCircle(36.dp)
            Text(
                "The Place to Be Thread",
                modifier = Modifier.align(Alignment.CenterVertically).padding(start = 12.dp),
                fontSize = 20.sp
            )
        }
        HorizontalDivider(modifier = Modifier.align(Alignment.BottomCenter))
    }
}

@Composable
fun SingleThreadMessage() {
    val divider = MaterialTheme.colors.divider
    Column(Modifier.padding(4.dp)) {
        Row {
            PreviewProfileCircle(18.dp)
            Text("John Stevens", modifier = Modifier.padding(horizontal = 8.dp), fontSize = 14.sp)
            Text("###", modifier = Modifier.padding(end = 8.dp), color = MaterialTheme.colors.secondary)
            Text("12min. ago", fontSize = 14.sp)
        }
        Row(Modifier.padding(start = 8.dp).wrapContentHeight()) {
            Text("Ut enim ad minima veniam, quis nostrum exercitationem ullam corporis suscipit laboriosam, nisi ut aliquid ex ea commodi consequatur? Quis autem vel eum iure reprehenderit qui in vel illum qui dolorem eum fugiat quo voluptas nulla pariatur. ", modifier = Modifier.padding(start = 16.dp))
        }
    }
}