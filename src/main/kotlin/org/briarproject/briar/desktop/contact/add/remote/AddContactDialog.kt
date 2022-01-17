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

package org.briarproject.briar.desktop.contact.add.remote

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.briarproject.briar.desktop.contact.add.remote.AddContactViewModel.AddContactError
import org.briarproject.briar.desktop.contact.add.remote.AddContactViewModel.AliasInvalidError
import org.briarproject.briar.desktop.contact.add.remote.AddContactViewModel.ErrorContactAlreadyExists
import org.briarproject.briar.desktop.contact.add.remote.AddContactViewModel.ErrorPendingAlreadyExists
import org.briarproject.briar.desktop.contact.add.remote.AddContactViewModel.LinkInvalidError
import org.briarproject.briar.desktop.contact.add.remote.AddContactViewModel.OwnLinkError
import org.briarproject.briar.desktop.contact.add.remote.AddContactViewModel.PublicKeyInvalidError
import org.briarproject.briar.desktop.contact.add.remote.AddContactViewModel.RemoteInvalidError
import org.briarproject.briar.desktop.dialogs.DialogType.ERROR
import org.briarproject.briar.desktop.dialogs.DialogType.WARNING
import org.briarproject.briar.desktop.theme.Orange500
import org.briarproject.briar.desktop.theme.Red500
import org.briarproject.briar.desktop.ui.Constants.DIALOG_WIDTH
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18nF
import org.briarproject.briar.desktop.utils.PreviewUtils
import org.briarproject.briar.desktop.utils.PreviewUtils.preview
import org.briarproject.briar.desktop.viewmodel.viewModel

const val link = "briar://ady23gvb2r76afe5zhxh5kvnh4b22zrcnxibn63tfknrdcwrw7zrs"

fun main() = preview(
    "visible" to true,
    "remote link" to "",
    "local link" to link,
    "alias" to "Alice",
    "error visible" to false,
    "error type" to PreviewUtils.Values(
        0,
        listOf(
            OwnLinkError(link),
            RemoteInvalidError(link),
            AliasInvalidError(link, ""),
            LinkInvalidError(link),
            PublicKeyInvalidError(link),
            ErrorContactAlreadyExists(link, "David", "chuck"),
            ErrorPendingAlreadyExists(link, "Frank", "chuck"),
        )
    ) { error -> error.javaClass.simpleName },
) {
    val localLink = getStringParameter("local link")
    AddContactDialog(
        onClose = { setBooleanParameter("visible", false) },
        visible = getBooleanParameter("visible"),
        remoteHandshakeLink = getStringParameter("remote link"),
        setRemoteHandshakeLink = { link -> setStringParameter("remote link", link) },
        alias = getStringParameter("alias"),
        setAddContactAlias = { alias -> setStringParameter("alias", alias) },
        handshakeLink = localLink,
        onSubmitAddContactDialog = { setBooleanParameter("error visible", true) },
        error = if (getBooleanParameter("error visible")) getGenericParameter("error type") else null,
        onErrorDialogDismissed = { setBooleanParameter("error visible", false) },
    )
}

@Composable
fun AddContactDialog(
    viewModel: AddContactViewModel = viewModel(),
) = AddContactDialog(
    viewModel::dismissDialog,
    viewModel.visible.value,
    viewModel.remoteHandshakeLink.value,
    viewModel::setRemoteHandshakeLink,
    viewModel.alias.value,
    viewModel::setAddContactAlias,
    viewModel.handshakeLink.value,
    viewModel::onSubmitAddContactDialog,
    viewModel.error.value,
    viewModel::clearError,
)

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddContactDialog(
    onClose: () -> Unit,
    visible: Boolean,
    remoteHandshakeLink: String,
    setRemoteHandshakeLink: (String) -> Unit,
    alias: String,
    setAddContactAlias: (String) -> Unit,
    handshakeLink: String,
    onSubmitAddContactDialog: () -> Unit,
    error: AddContactError?,
    onErrorDialogDismissed: () -> Unit,
) {
    if (!visible) {
        return
    }
    AlertDialog(
        onDismissRequest = onClose,
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(Modifier.fillMaxWidth().padding(vertical = 16.dp)) {
                    Text(
                        text = "Add Contact at a Distance",
                        fontSize = 24.sp,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "Contact's Link",
                        Modifier.width(128.dp).align(Alignment.CenterVertically),
                    )
                    TextField(
                        remoteHandshakeLink,
                        setRemoteHandshakeLink,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "Contact's Name",
                        Modifier.width(128.dp).align(Alignment.CenterVertically),
                    )
                    TextField(
                        alias,
                        setAddContactAlias,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "Your Link",
                        modifier = Modifier.width(128.dp).align(Alignment.CenterVertically),
                    )
                    TextField(
                        handshakeLink,
                        onValueChange = {},
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSubmitAddContactDialog()
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onClose
            ) {
                Text("Cancel", color = MaterialTheme.colors.onSurface)
            }
        },
    )
    if (error != null) {
        val (type, title, message) = errorMessage(error)
        val (icon, color) = when (type) {
            WARNING -> Icons.Filled.Warning to Orange500
            ERROR -> Icons.Filled.Error to Red500
        }
        AlertDialog(
            onDismissRequest = onErrorDialogDismissed,
            confirmButton = {
                TextButton(onErrorDialogDismissed) {
                    Text(i18n("ok"))
                }
            },
            modifier = Modifier.widthIn(min = DIALOG_WIDTH),
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = color
                    )
                    Text(title)
                }
            },
            text = { Text(message) }
        )
    }
}

fun errorMessage(error: AddContactError) = when (error) {
    is OwnLinkError -> Triple(ERROR, i18n("error"), i18n("introduction.error.own_link"))
    is RemoteInvalidError -> Triple(ERROR, i18n("error"), i18n("introduction.error.remote_invalid"))
    is AliasInvalidError -> Triple(ERROR, i18n("error"), i18n("introduction.error.alias_invalid"))
    is LinkInvalidError -> Triple(ERROR, i18n("error"), i18nF("introduction.error.link_invalid", error.link))
    is PublicKeyInvalidError -> Triple(
        ERROR, i18n("error"),
        i18nF("introduction.error.public_key_invalid", error.link)
    )
    is ErrorContactAlreadyExists -> {
        val intro = i18nF("introduction.error.contact_already_exists", error.existingName)
        var explanation = i18nF("introduction.error.duplicate_contact_explainer", error.existingName, error.alias)
        Triple(WARNING, i18n("introduction.error.adding_failed"), (intro + "\n\n" + explanation))
    }
    is ErrorPendingAlreadyExists -> {
        val intro = i18nF("introduction.error.pending_contact_already_exists", error.existingAlias)
        var explanation = i18nF("introduction.error.duplicate_contact_explainer", error.existingAlias, error.alias)
        Triple(WARNING, i18n("introduction.error.adding_failed"), (intro + "\n\n" + explanation))
    }
}
