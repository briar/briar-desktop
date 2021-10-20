package org.briarproject.briar.desktop.contact.add.remote

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddContactDialog(viewModel: AddContactViewModel, onClose: () -> Unit) {
    LaunchedEffect("fetchHandshake") {
        // todo: should instead be done automatically as soon as DB is loaded -> in view model
        viewModel.fetchHandshakeLink()
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
                        viewModel.remoteHandshakeLink.value,
                        viewModel::setRemoteHandshakeLink,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "Contact's Name",
                        Modifier.width(128.dp).align(Alignment.CenterVertically),
                    )
                    TextField(
                        viewModel.alias.value,
                        viewModel::setAddContactAlias,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "Your Link",
                        modifier = Modifier.width(128.dp).align(Alignment.CenterVertically),
                    )
                    TextField(
                        viewModel.handshakeLink.value,
                        onValueChange = {},
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = { viewModel.onSubmitAddContactDialog(); onClose() }) {
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
        modifier = Modifier.size(600.dp, 300.dp),
    )
}
