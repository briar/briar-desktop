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
import androidx.compose.material.ButtonType.NEUTRAL
import androidx.compose.material.DialogButton
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Text
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
import org.briarproject.briar.desktop.contact.add.remote.AddContactViewModel.ContactAlreadyExistsError
import org.briarproject.briar.desktop.contact.add.remote.AddContactViewModel.LinkInvalidError
import org.briarproject.briar.desktop.contact.add.remote.AddContactViewModel.OwnLinkError
import org.briarproject.briar.desktop.contact.add.remote.AddContactViewModel.PendingAlreadyExistsError
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
import org.jetbrains.annotations.NonNls

@NonNls
const val link = "briar://ady23gvb2r76afe5zhxh5kvnh4b22zrcnxibn63tfknrdcwrw7zrs"

@Suppress("HardCodedStringLiteral")
fun main() = preview(
    "visible" to true,
    "remote link" to "",
    "local link" to link,
    "alias" to "Alice",
    "error visible" to false,
    "error type" to PreviewUtils.DropDownValues(
        0,
        listOf(
            OwnLinkError::class.simpleName!!,
            RemoteInvalidError::class.simpleName!!,
            AliasInvalidError::class.simpleName!!,
            LinkInvalidError::class.simpleName!!,
            PublicKeyInvalidError::class.simpleName!!,
            ContactAlreadyExistsError::class.simpleName!!,
            PendingAlreadyExistsError::class.simpleName!!,
        )
    ),
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
        error = if (getBooleanParameter("error visible")) mapErrors(getStringParameter("error type")) else null,
        onErrorDialogDismissed = { setBooleanParameter("error visible", false) },
    )
}

@Suppress("HardCodedStringLiteral")
private fun PreviewUtils.PreviewScope.mapErrors(name: String?): AddContactError? = when (name) {
    OwnLinkError::class.simpleName!! -> OwnLinkError(link)
    RemoteInvalidError::class.simpleName!! -> RemoteInvalidError(link)
    AliasInvalidError::class.simpleName!! -> AliasInvalidError(link, "")
    LinkInvalidError::class.simpleName!! -> LinkInvalidError(link)
    PublicKeyInvalidError::class.simpleName!! -> PublicKeyInvalidError(link)
    ContactAlreadyExistsError::class.simpleName!! -> ContactAlreadyExistsError(
        link, "David", getStringParameter("alias")
    )
    PendingAlreadyExistsError::class.simpleName!! -> PendingAlreadyExistsError(
        link, "Frank", getStringParameter("alias")
    )
    else -> null
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
                        text = i18n("conversation.add.contact.dialog.title"),
                        fontSize = 24.sp,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        i18n("conversation.add.contact.dialog.contact_link"),
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
                        "Contact's name",
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
                        "Your link",
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
        confirmButton = { DialogButton(onClick = onSubmitAddContactDialog, text = i18n("add"), type = NEUTRAL) },
        dismissButton = { DialogButton(onClick = onClose, text = i18n("cancel"), type = NEUTRAL) },
    )
    if (error != null) {
        val (type, title, message) = errorMessage(error)
        val (icon, color) = when (type) {
            WARNING -> Icons.Filled.Warning to Orange500
            ERROR -> Icons.Filled.Error to Red500
        }
        AlertDialog(
            onDismissRequest = onErrorDialogDismissed,
            confirmButton = { DialogButton(onClick = onErrorDialogDismissed, text = i18n("ok"), type = NEUTRAL) },
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
    is ContactAlreadyExistsError -> {
        val intro = i18nF("introduction.error.contact_already_exists", error.existingName)
        var explanation = i18nF("introduction.error.duplicate_contact_explainer", error.existingName, error.alias)
        Triple(WARNING, i18n("introduction.error.adding_failed"), (intro + "\n\n" + explanation))
    }
    is PendingAlreadyExistsError -> {
        val intro = i18nF("introduction.error.pending_contact_already_exists", error.existingAlias)
        var explanation = i18nF("introduction.error.duplicate_contact_explainer", error.existingAlias, error.alias)
        Triple(WARNING, i18n("introduction.error.adding_failed"), (intro + "\n\n" + explanation))
    }
}
