package org.briarproject.briar.desktop.privategroups

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.briarproject.bramble.api.sync.GroupId
import org.briarproject.briar.desktop.contact.PreviewProfileCircle
import org.briarproject.briar.desktop.conversation.ConversationInput
import org.briarproject.briar.desktop.navigation.SIDEBAR_WIDTH
import org.briarproject.briar.desktop.theme.divider
import org.briarproject.briar.desktop.theme.surfaceVariant
import org.briarproject.briar.desktop.ui.Constants.CONTACTLIST_WIDTH
import org.briarproject.briar.desktop.ui.Loader
import org.briarproject.briar.desktop.viewmodel.viewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ThreadedConversationScreen(
    groupId: GroupId,
    viewModel: ThreadedConversationViewModel = viewModel(),
    singleThreadViewModel: SingleThreadViewModel = viewModel(),
) {
    LaunchedEffect(groupId) {
        viewModel.setGroupId(groupId)
    }

    val contactItem = viewModel.contactItem.value

    if (contactItem == null) {
        Loader()
        return
    }

    val (infoDrawer, setInfoDrawer) = remember { mutableStateOf(false) }
    val (contactDrawerState, setDrawerState) = remember { mutableStateOf(PrivateGroupDrawerState.VIEW_THREAD) }
    BoxWithConstraints(Modifier.fillMaxSize()) {
        val animatedInfoDrawerOffsetX by animateDpAsState(if (infoDrawer) (-275).dp else 0.dp)
        Scaffold(
            topBar = {
                ThreadedConversationHeader(
                    contactItem,
                    onMakeIntroduction = {
                        setInfoDrawer(true)
                    }
                )
            },
            content = { padding ->
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    // reverseLayout to display most recent message (index 0) at the bottom
                    contentPadding = PaddingValues(8.dp),
                    modifier = Modifier.padding(padding).fillMaxHeight()
                ) {
                    item {
                        Card {
                            ExperimentalThreadedForumPost(singleThreadViewModel) {
                                ExperimentalThreadedForumPost(singleThreadViewModel) {}
                            }
                        }
                    }
                    item {
                        Card {
                            ExperimentalThreadedForumPost(singleThreadViewModel) {
                                ExperimentalThreadedForumPost(singleThreadViewModel) {
                                    ExperimentalThreadedForumPost(singleThreadViewModel) {
                                        ExperimentalThreadedForumPost(singleThreadViewModel) {
                                            ExperimentalThreadedForumPost(singleThreadViewModel) {

                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    item {
                        Card {
                            ExperimentalThreadedForumPost(singleThreadViewModel) {}
                        }
                    }
                    item {
                        Card {
                            ExperimentalThreadedForumPost(singleThreadViewModel) {}
                        }
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
        AnimatedVisibility(singleThreadViewModel.isOpen.value, enter = fadeIn(), exit = fadeOut()) {
            SingleThreadScreen(groupId, viewModel, singleThreadViewModel)
        }
        if (infoDrawer) {
            // TODO Find non-hacky way of setting scrim on entire app
            Box(
                Modifier.offset(-(CONTACTLIST_WIDTH + SIDEBAR_WIDTH))
                    .requiredSize(maxWidth + CONTACTLIST_WIDTH + SIDEBAR_WIDTH, maxHeight)
                    .background(Color(0, 0, 0, 100))
                    .clickable(
                        // prevent visual indication
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { setInfoDrawer(false) }
            )
            Column(
                modifier = Modifier.fillMaxHeight().width(CONTACTLIST_WIDTH)
                    .offset(maxWidth + animatedInfoDrawerOffsetX)
                    .background(
                        MaterialTheme.colors.surfaceVariant,
                        RoundedCornerShape(topStart = 10.dp, bottomStart = 10.dp)
                    )
            ) {
                PrivateGroupDrawer(PrivateGroupDrawerState.VIEW_THREAD)
            }
        }
    }
}

@Composable
fun ExperimentalThreadedForumPost(singleThreadViewModel: SingleThreadViewModel, content: @Composable (ColumnScope.() -> Unit),) {
    val divider = MaterialTheme.colors.divider
    Column(
        Modifier.padding(start = 8.dp, end = 8.dp, top = 8.dp).wrapContentHeight().drawBehind {
            val height = size.height
            drawLine(divider, Offset(13f, 22f), Offset(13f, height))
        }
    ) {
        Column(Modifier.clickable { singleThreadViewModel.setViewState(true) }.padding(4.dp)) {
            Row {
                PreviewProfileCircle(18.dp)
                Text("John Stevens", modifier = Modifier.padding(horizontal = 8.dp), fontSize = 14.sp)
                Text("###", modifier = Modifier.padding(end = 8.dp), color = MaterialTheme.colors.secondary)
                Text("12min. ago", fontSize = 14.sp)
            }
            Row(Modifier.padding(start = 8.dp).wrapContentHeight()) {
                Text("Ut enim ad minima veniam, quis nostrum exercitationem ullam corporis suscipit laboriosam, nisi ut aliquid ex ea commodi consequatur? Quis autem vel eum iure reprehenderit qui in vel illum qui dolorem eum fugiat quo voluptas nulla pariatur. ", modifier = Modifier.padding(start = 16.dp))
            }
            TextButton(onClick = {}, modifier = Modifier.padding(start = 12.dp)) {
                Text("Reply")
            }
        }
        Column(Modifier.padding(start = 8.dp)) {
            content()
        }
    }
}
