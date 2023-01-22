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

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import org.briarproject.bramble.api.contact.ContactId
import org.briarproject.briar.desktop.introduction.IntroductionDrawerContent
import org.briarproject.briar.desktop.ui.Loader
import org.briarproject.briar.desktop.ui.getInfoDrawerHandler
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

    val infoDrawerHandler = getInfoDrawerHandler()

    val (deleteAllMessagesDialogVisible, setDeleteAllMessagesDialog) = remember { mutableStateOf(false) }
    val (changeAliasDialogVisible, setChangeAliasDialog) = remember { mutableStateOf(false) }
    val (deleteContactDialogVisible, setDeleteContactDialog) = remember { mutableStateOf(false) }

    BoxWithConstraints(Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                ConversationHeader(
                    contactItem,
                    onMakeIntroduction = {
                        infoDrawerHandler.open {
                            IntroductionDrawerContent(
                                contactItem,
                                close = { reload ->
                                    infoDrawerHandler.close()
                                    if (reload) {
                                        // reload all messages to also show introduction message
                                        // todo: might be better to have an event to react to, also for (all) outgoing messages
                                        viewModel.reloadMessages()
                                    }
                                },
                            )
                        }
                    },
                    onDeleteAllMessages = {
                        setDeleteAllMessagesDialog(true)
                    },
                    onChangeAlias = {
                        setChangeAliasDialog(true)
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

        DeleteAllMessagesConfirmationDialog(
            isVisible = deleteAllMessagesDialogVisible,
            close = { setDeleteAllMessagesDialog(false) },
            onDelete = viewModel::deleteAllMessages
        )

        DeleteAllMessagesFailedDialog(
            deletionResult = viewModel.deletionResult.value,
            close = viewModel::confirmDeletionResult
        )

        ChangeAliasDialog(
            isVisible = changeAliasDialogVisible,
            close = { setChangeAliasDialog(false) },
            onConfirm = viewModel::changeAlias,
            onCancel = viewModel::resetAlias,
            alias = viewModel.newAlias.value,
            setAlias = viewModel::setNewAlias,
        )

        DeleteContactConfirmationDialog(
            isVisible = deleteContactDialogVisible,
            close = { setDeleteContactDialog(false) },
            onDelete = viewModel::deleteContact
        )
    }
}
