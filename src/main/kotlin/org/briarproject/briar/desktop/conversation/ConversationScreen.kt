/*
 * Briar Desktop
 * Copyright (C) 2021-2022 The Briar Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.briarproject.briar.desktop.conversation

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.briarproject.bramble.api.contact.ContactId
import org.briarproject.briar.desktop.contact.ContactInfoDrawer
import org.briarproject.briar.desktop.contact.ContactInfoDrawerState
import org.briarproject.briar.desktop.navigation.SIDEBAR_WIDTH
import org.briarproject.briar.desktop.theme.surfaceVariant
import org.briarproject.briar.desktop.ui.Constants.COLUMN_WIDTH
import org.briarproject.briar.desktop.ui.Loader
import org.briarproject.briar.desktop.viewmodel.viewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ConversationScreen(
    contactId: ContactId,
    viewModel: ConversationViewModel = viewModel(),
) {
    LaunchedEffect(contactId) {
        viewModel.setContactId(contactId)
    }

    val contactItem = viewModel.contactItem.value

    if (contactItem == null) {
        Loader()
        return
    }

    val (infoDrawer, setInfoDrawer) = remember { mutableStateOf(false) }
    val (contactDrawerState, setDrawerState) = remember { mutableStateOf(ContactInfoDrawerState.MakeIntro) }
    val (deleteAllMessagesDialogVisible, setDeleteAllMessagesDialog) = remember { mutableStateOf(false) }
    val (deleteContactDialogVisible, setDeleteContactDialog) = remember { mutableStateOf(false) }

    BoxWithConstraints(Modifier.fillMaxSize()) {
        val animatedInfoDrawerOffsetX by animateDpAsState(if (infoDrawer) (-275).dp else 0.dp)
        Scaffold(
            topBar = {
                ConversationHeader(
                    contactItem,
                    onMakeIntroduction = {
                        setInfoDrawer(true)
                    },
                    onDeleteAllMessages = {
                        setDeleteAllMessagesDialog(true)
                    },
                    onDeleteContact = {
                        setDeleteContactDialog(true)
                    }
                )
            },
            content = { padding ->
                if (viewModel.loadingMessages.value) {
                    Loader()
                    return@Scaffold
                }
                ConversationList(
                    padding,
                    viewModel.messages,
                    viewModel.initialFirstUnreadMessageIndex.value,
                    viewModel.currentUnreadMessagesInfo.value,
                    viewModel.onMessageAddedToBottom,
                    viewModel::markMessagesRead,
                    viewModel::respondToRequest,
                    viewModel::deleteMessage,
                )
            },
            bottomBar = {
                ConversationInput(
                    viewModel.newMessage.value,
                    viewModel::setNewMessage,
                    viewModel.newMessageImage.value,
                    viewModel::setNewMessageImage,
                    viewModel::sendMessage,
                )
            },
        )

        if (infoDrawer) {
            // TODO Find non-hacky way of setting scrim on entire app
            Box(
                Modifier.offset(-(COLUMN_WIDTH + SIDEBAR_WIDTH))
                    .requiredSize(maxWidth + COLUMN_WIDTH + SIDEBAR_WIDTH, maxHeight)
                    .background(Color(0, 0, 0, 100))
                    .clickable(
                        // prevent visual indication
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { setInfoDrawer(false) }
            )
            Column(
                modifier = Modifier.fillMaxHeight().width(COLUMN_WIDTH)
                    .offset(maxWidth + animatedInfoDrawerOffsetX)
                    .background(
                        MaterialTheme.colors.surfaceVariant,
                        RoundedCornerShape(topStart = 10.dp, bottomStart = 10.dp)
                    )
            ) {
                ContactInfoDrawer(
                    contactItem,
                    closeInfoDrawer = { reload ->
                        setInfoDrawer(false)
                        if (reload) {
                            // reload all messages to also show introduction message
                            // todo: might be better to have an event to react to, also for (all) outgoing messages
                            viewModel.reloadMessages()
                        }
                    },
                    contactDrawerState
                )
            }
        }

        DeleteAllMessagesConfirmationDialog(
            isVisible = deleteAllMessagesDialogVisible,
            close = { setDeleteAllMessagesDialog(false) },
            onDelete = viewModel::deleteAllMessages
        )

        DeleteAllMessagesFailedDialog(
            deletionResult = viewModel.deletionResult.value,
            close = viewModel::confirmDeletionResult
        )

        DeleteContactConfirmationDialog(
            isVisible = deleteContactDialogVisible,
            close = { setDeleteContactDialog(false) },
            onDelete = viewModel::deleteContact
        )
    }
}
