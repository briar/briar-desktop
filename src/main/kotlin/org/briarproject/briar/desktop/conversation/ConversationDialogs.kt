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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.width
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonType.DESTRUCTIVE
import androidx.compose.material.ButtonType.NEUTRAL
import androidx.compose.material.DialogButton
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.briarproject.briar.api.conversation.DeletionResult
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n
import org.briarproject.briar.desktop.utils.PreviewUtils.preview

@Suppress("HardCodedStringLiteral")
fun main() = preview(
    "introduction_pending" to false,
    "invitation_pending" to false,
    "introduction_not_all" to false,
    "invitation_not_all" to false,
) {
    var confirmationDialog by remember { mutableStateOf(false) }
    var failedDialog by remember { mutableStateOf(false) }
    var changeAliasDialog by remember { mutableStateOf(false) }
    val deletionResult by derivedStateOf {
        if (!failedDialog) null else
            DeletionResult().apply {
                if (getBooleanParameter("introduction_pending")) addIntroductionSessionInProgress()
                if (getBooleanParameter("invitation_pending")) addInvitationSessionInProgress()
                if (getBooleanParameter("introduction_not_all")) addIntroductionNotAllSelected()
                if (getBooleanParameter("invitation_not_all")) addInvitationNotAllSelected()
            }
    }

    Column {
        Button(onClick = { confirmationDialog = true }) {
            Text("Show confirmation dialog")
        }

        Button(onClick = { failedDialog = true }) {
            Text("Show deletion failed dialog")
        }

        Button(onClick = { changeAliasDialog = true }) {
            Text("Show change alias dialog")
        }
    }

    DeleteAllMessagesConfirmationDialog(
        isVisible = confirmationDialog,
        close = { confirmationDialog = false },
    )

    DeleteAllMessagesFailedDialog(deletionResult) { failedDialog = false }

    val (alias, setAlias) = remember { mutableStateOf("Alice") }
    ChangeAliasDialog(
        isVisible = changeAliasDialog,
        alias = alias,
        setAlias = setAlias,
        close = { changeAliasDialog = false },
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DeleteAllMessagesConfirmationDialog(
    isVisible: Boolean,
    close: () -> Unit,
    onDelete: () -> Unit = {},
    onCancel: () -> Unit = {},
) {
    if (!isVisible) return

    AlertDialog(
        onDismissRequest = close,
        title = {
            Text(
                text = i18n("conversation.delete.all.dialog.title"),
                modifier = Modifier.width(IntrinsicSize.Max)
            )
        },
        text = {
            Text(i18n("conversation.delete.all.dialog.message"))
        },
        dismissButton = { DialogButton(onClick = { close(); onCancel() }, text = i18n("cancel"), type = NEUTRAL) },
        confirmButton = { DialogButton(onClick = { close(); onDelete() }, text = i18n("delete"), type = DESTRUCTIVE) },
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DeleteAllMessagesFailedDialog(
    deletionResult: DeletionResult?,
    close: () -> Unit,
) {
    if (deletionResult == null) return

    val message = buildList {
        when {
            // get failures the user cannot immediately resolve
            deletionResult.hasIntroductionSessionInProgress() &&
                deletionResult.hasInvitationSessionInProgress() ->
                add(i18n("conversation.delete.failed.dialog.message.ongoing_both"))
            deletionResult.hasIntroductionSessionInProgress() ->
                add(i18n("conversation.delete.failed.dialog.message.ongoing_introductions"))
            deletionResult.hasInvitationSessionInProgress() ->
                add(i18n("conversation.delete.failed.dialog.message.ongoing_invitations"))
        }
        when {
            // add problems the user can resolve
            deletionResult.hasNotAllIntroductionSelected() &&
                deletionResult.hasNotAllInvitationSelected() ->
                add(i18n("conversation.delete.failed.dialog.message.not_all_selected_both"))
            deletionResult.hasNotAllIntroductionSelected() ->
                add(i18n("conversation.delete.failed.dialog.message.not_all_selected_introductions"))
            deletionResult.hasNotAllInvitationSelected() ->
                add(i18n("conversation.delete.failed.dialog.message.not_all_selected_invitations"))
        }
    }.joinToString("\n\n")

    AlertDialog(
        onDismissRequest = close,
        title = {
            Text(
                text = i18n("conversation.delete.failed.dialog.title"),
                modifier = Modifier.width(IntrinsicSize.Max)
            )
        },
        text = {
            Text(message)
        },
        confirmButton = { DialogButton(onClick = close, text = i18n("ok"), type = NEUTRAL) },
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ChangeAliasDialog(
    isVisible: Boolean,
    alias: String,
    setAlias: (String) -> Unit,
    close: () -> Unit,
    onConfirm: () -> Unit = {},
    onCancel: () -> Unit = {},
) {
    if (!isVisible) return

    AlertDialog(
        onDismissRequest = { close; onCancel() },
        title = {
            Text(
                text = i18n("conversation.change.alias.dialog.title"),
                modifier = Modifier.width(IntrinsicSize.Max)
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(i18n("conversation.change.alias.dialog.description"))
                TextField(alias, setAlias)
            }
        },
        dismissButton = { DialogButton(onClick = { close(); onCancel() }, text = i18n("cancel"), type = NEUTRAL) },
        confirmButton = { DialogButton(onClick = { close(); onConfirm() }, text = i18n("change"), type = NEUTRAL) },
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DeleteContactConfirmationDialog(
    isVisible: Boolean,
    close: () -> Unit,
    onDelete: () -> Unit = {},
    onCancel: () -> Unit = {},
) {
    if (!isVisible) return

    AlertDialog(
        onDismissRequest = close,
        title = {
            Text(
                text = i18n("conversation.delete.contact.dialog.title"),
                modifier = Modifier.width(IntrinsicSize.Max)
            )
        },
        text = {
            Text(i18n("conversation.delete.contact.dialog.message"))
        },
        dismissButton = { DialogButton(onClick = { close(); onCancel() }, text = i18n("cancel"), type = NEUTRAL) },
        confirmButton = { DialogButton(onClick = { close(); onDelete() }, text = i18n("delete"), type = DESTRUCTIVE) },
    )
}
