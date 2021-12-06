package org.briarproject.briar.desktop.contact.add.remote

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.TooltipPlacement
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarHost
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.NorthEast
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SouthWest
import androidx.compose.material.rememberDrawerState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.briarproject.briar.desktop.theme.surfaceVariant
import org.briarproject.briar.desktop.ui.Constants.HEADER_SIZE
import org.briarproject.briar.desktop.utils.PreviewUtils.preview
import org.briarproject.briar.desktop.viewmodel.viewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddContactDialog(
    onClose: () -> Unit,
    viewModel: AddContactViewModel = viewModel(),
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

@OptIn(ExperimentalFoundationApi::class)
fun main() = preview() {
    val clipboardManager = LocalClipboardManager.current
    val demoBriarLink = "briar://aavpaa6faa2utx5locopyuldr3ceuefe4c3rxn6pj2kb72mz4yxrs"
    var contactLink by remember { mutableStateOf("") }
    var contactNickname by remember { mutableStateOf("") }
    MaterialTheme {
        val scaffoldState = rememberScaffoldState()
        val coroutineScope = rememberCoroutineScope()
        Scaffold(
            topBar = {
                Box(Modifier.fillMaxWidth().height(HEADER_SIZE)) {
                    Text("Add Contact at a Distance", style = MaterialTheme.typography.h5, modifier = Modifier.padding(8.dp))
                }
            },
            bottomBar = {
                Box(Modifier.fillMaxWidth().height(HEADER_SIZE).padding(4.dp)) {
                    Row(Modifier.align(Alignment.CenterEnd)) {
                        TextButton({}, colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colors.error)) {
                            Text("CANCEL")
                        }
                        Button({}, modifier = Modifier.padding(start = 8.dp), ) {
                            Text("ADD")
                        }
                    }
                }
            },
            scaffoldState = scaffoldState,
            content = {
            Column(Modifier.fillMaxSize().padding(horizontal = 4.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.NorthEast, "northeast")
                    Text("Give this link to the contact you want to add", Modifier.padding(8.dp), style = MaterialTheme.typography.body1)
                }
                Box(Modifier.fillMaxWidth().background(MaterialTheme.colors.surfaceVariant, RoundedCornerShape(4.dp)).clickable {
                    clipboardManager.setText(AnnotatedString(demoBriarLink))
                    coroutineScope.launch { // using the `coroutineScope` to `launch` showing the snackbar
                        // taking the `snackbarHostState` from the attached `scaffoldState`
                        scaffoldState.snackbarHostState.showSnackbar(
                            message = "Briar Link Copied to Clipboard",
                        )
                    }
                }) {
                    Text(
                        demoBriarLink,
                        style = TextStyle(fontSize = 12.sp, fontFamily = FontFamily.Monospace, letterSpacing = (-0.5).sp),
                        modifier = Modifier.padding(start = 16.dp, end = 36.dp, top = 16.dp, bottom = 16.dp),
                        maxLines = 1,
                    )
                    TooltipArea(
                        tooltip = {
                            // composable tooltip content
                            Surface(
                                modifier = Modifier.shadow(4.dp),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "Copy",
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        },
                        modifier = Modifier.align(Alignment.CenterEnd),
                        delayMillis = 200, // in milliseconds
                        tooltipPlacement = TooltipPlacement.ComponentRect(
                            alignment = Alignment.BottomCenter,
                        )) {
                        Icon(Icons.Filled.ContentCopy, "yoo", modifier = Modifier.padding(8.dp))
                    }
                }
                Row(Modifier.padding(top = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.SouthWest, "southwest")
                    Text("Enter the link from your contact here", Modifier.padding(horizontal = 8.dp), style = MaterialTheme.typography.body1)
                }
                OutlinedTextField(contactLink, {contactLink = it}, label = { Text("Contact's Link")}, modifier = Modifier.fillMaxWidth(), trailingIcon = {
                    TooltipArea(
                        tooltip = {
                            // composable tooltip content
                            Surface(
                                modifier = Modifier.shadow(4.dp),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "Paste",
                                    modifier = Modifier.padding(8.dp)
                                )
                            }
                        },
                        modifier = Modifier.padding(start = 40.dp),
                        delayMillis = 200, // in milliseconds
                        tooltipPlacement = TooltipPlacement.ComponentRect(
                            alignment = Alignment.BottomCenter,
                        )) {
                        IconButton({
                            contactLink = clipboardManager.getText().toString()
                            coroutineScope.launch { // using the `coroutineScope` to `launch` showing the snackbar
                                // taking the `snackbarHostState` from the attached `scaffoldState`

                                scaffoldState.snackbarHostState.showSnackbar(
                                    message = "Pasted from Clipboard",
                                )
                            }
                        }) {
                            Icon(Icons.Filled.ContentPaste, "yoo", modifier = Modifier.padding(8.dp), tint = MaterialTheme.colors.onSurface)
                        }
                    }
                })
                Row(Modifier.padding(top = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Person, "southwest")
                    Text("Give your contact a nickname. Only you can see it", Modifier.padding(horizontal = 8.dp), style = MaterialTheme.typography.body1)
                }
                OutlinedTextField(contactNickname, {contactNickname = it}, label = { Text("Enter a nickname")}, modifier = Modifier.fillMaxWidth())
            }
        })
    }
}
