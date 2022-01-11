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
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.briarproject.briar.desktop.utils.PreviewUtils.preview
import org.briarproject.briar.desktop.viewmodel.viewModel

fun main() = preview(
    "visible" to true,
    "remote link" to "",
    "local link" to "briar://ady23gvb2r76afe5zhxh5kvnh4b22zrcnxibn63tfknrdcwrw7zrs",
    "alias" to "Alice",
) {
    if (getBooleanParameter("visible")) {
        AddContactDialog(
            onClose = { setBooleanParameter("visible", false) },
            remoteHandshakeLink = getStringParameter("remote link"),
            setRemoteHandshakeLink = { link -> setStringParameter("remote link", link) },
            alias = getStringParameter("alias"),
            setAddContactAlias = { alias -> setStringParameter("alias", alias) },
            handshakeLink = getStringParameter("local link"),
            onSubmitAddContactDialog = {}
        )
    }
}

@Composable
fun AddContactDialog(
    onClose: () -> Unit,
    viewModel: AddContactViewModel = viewModel(),
) = AddContactDialog(
    onClose = onClose,
    viewModel.remoteHandshakeLink.value,
    viewModel::setRemoteHandshakeLink,
    viewModel.alias.value,
    viewModel::setAddContactAlias,
    viewModel.handshakeLink.value,
    viewModel::onSubmitAddContactDialog,
)

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddContactDialog(
    onClose: () -> Unit,
    remoteHandshakeLink: String,
    setRemoteHandshakeLink: (String) -> Unit,
    alias: String,
    setAddContactAlias: (String) -> Unit,
    handshakeLink: String,
    onSubmitAddContactDialog: () -> Unit,
) {
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
            Button(onClick = { onSubmitAddContactDialog(); onClose() }) {
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
}
