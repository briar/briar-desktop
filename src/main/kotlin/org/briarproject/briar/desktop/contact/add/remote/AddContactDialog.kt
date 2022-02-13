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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.NorthEast
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SouthWest
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberDialogState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
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
import org.briarproject.briar.desktop.theme.surfaceVariant
import org.briarproject.briar.desktop.ui.Constants.DIALOG_WIDTH
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18nF
import org.briarproject.briar.desktop.utils.PreviewUtils
import org.briarproject.briar.desktop.utils.PreviewUtils.preview
import org.briarproject.briar.desktop.viewmodel.viewModel
import java.awt.Dimension

@Suppress("HardCodedStringLiteral")
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

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
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
    Dialog(
        title = i18n("contact.add.title_dialog"),
        onCloseRequest = onClose,
        state = rememberDialogState(
            position = WindowPosition(Alignment.Center),
            size = DpSize(width = 560.dp, height = 520.dp),
        ),
    ) {
        window.minimumSize = Dimension(360, 512)
        val clipboardManager = LocalClipboardManager.current
        val scaffoldState = rememberScaffoldState()
        val coroutineScope = rememberCoroutineScope()
        val aliasFocusRequester = remember { FocusRequester() }
        Surface {
            Scaffold(
                modifier = Modifier.padding(horizontal = 24.dp).padding(top = 24.dp, bottom = 12.dp),
                topBar = {
                    Box(Modifier.fillMaxWidth()) {
                        Text(
                            i18n("contact.add.remote.title"),
                            style = MaterialTheme.typography.h6,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }
                },
                scaffoldState = scaffoldState,
                content = {
                    Column(Modifier.fillMaxSize()) {
                        if (error != null) {
                            AddContactErrorDialog(error, onErrorDialogDismissed)
                        }
                        OwnLink(
                            handshakeLink,
                            clipboardManager,
                            coroutineScope,
                            scaffoldState,
                        )
                        ContactLink(
                            remoteHandshakeLink,
                            setRemoteHandshakeLink,
                            clipboardManager,
                            coroutineScope,
                            scaffoldState,
                            aliasFocusRequester,
                        )
                        Alias(
                            alias,
                            setAddContactAlias,
                            aliasFocusRequester,
                            onSubmitAddContactDialog,
                        )
                    }
                },
                bottomBar = {
                    Box(Modifier.fillMaxWidth()) {
                        Row(Modifier.align(Alignment.CenterEnd)) {
                            TextButton(
                                onClose,
                                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colors.error)
                            ) {
                                Text(i18n("cancel"))
                            }
                            Button(onSubmitAddContactDialog, modifier = Modifier.padding(start = 8.dp)) {
                                Text(i18n("add"))
                            }
                        }
                    }
                },
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddContactErrorDialog(error: AddContactError, onErrorDialogDismissed: () -> Unit) {
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OwnLink(
    handshakeLink: String,
    clipboardManager: ClipboardManager,
    coroutineScope: CoroutineScope,
    scaffoldState: ScaffoldState
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Filled.NorthEast, "contact.add.remote.outgoing_arrow")
        Text(
            i18n("contact.add.remote.your_link"),
            Modifier.padding(8.dp),
            fontSize = 14.sp,
        )
    }
    Box(
        Modifier.fillMaxWidth()
            .background(MaterialTheme.colors.surfaceVariant, RoundedCornerShape(4.dp))
            .clickable {
                clipboardManager.setText(AnnotatedString(handshakeLink))
                coroutineScope.launch {
                    scaffoldState.snackbarHostState.showSnackbar(
                        message = i18n("contact.add.remote.link_copied_snackbar"),
                        duration = SnackbarDuration.Short,
                    )
                }
            }
    ) {
        Text(
            handshakeLink,
            style = TextStyle(
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                letterSpacing = (-0.5).sp
            ),
            modifier = Modifier.padding(
                start = 16.dp,
                end = 36.dp,
                top = 16.dp,
                bottom = 16.dp
            ),
        )
        TooltipArea(
            tooltip = {
                Surface(
                    modifier = Modifier.shadow(4.dp),
                    shape = RoundedCornerShape(4.dp),
                ) {
                    Text(
                        text = i18n("contact.add.remote.copy_tooltip"),
                        modifier = Modifier.padding(8.dp)
                    )
                }
            },
            modifier = Modifier.align(Alignment.CenterEnd),
            delayMillis = 200,
            tooltipPlacement = TooltipPlacement.ComponentRect(
                alignment = Alignment.BottomCenter,
            )
        ) {
            Icon(Icons.Filled.ContentCopy, "contact.add.remote.contact_link", modifier = Modifier.padding(8.dp))
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ContactLink(
    remoteHandshakeLink: String,
    setRemoteHandshakeLink: (String) -> Unit,
    clipboardManager: ClipboardManager,
    coroutineScope: CoroutineScope,
    scaffoldState: ScaffoldState,
    aliasFocusRequester: FocusRequester,
) {
    Row(Modifier.padding(top = 16.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Filled.SouthWest, "contact.add.remote.incoming_arrow")
        Text(
            i18n("contact.add.remote.contact_link"),
            Modifier.padding(horizontal = 8.dp),
            fontSize = 14.sp,
        )
    }
    OutlinedTextField(
        remoteHandshakeLink,
        setRemoteHandshakeLink,
        label = { Text(i18n("contact.add.remote.contact_link_hint")) },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Next),
        singleLine = true,
        onEnter = { aliasFocusRequester.requestFocus() },
        textStyle = TextStyle(
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace,
            letterSpacing = (-0.5).sp,
        ),
        trailingIcon = {
            TooltipArea(
                tooltip = {
                    Surface(
                        modifier = Modifier.shadow(4.dp),
                        shape = RoundedCornerShape(4.dp),
                    ) {
                        Text(
                            text = i18n("contact.add.remote.paste_tooltip"),
                            modifier = Modifier.padding(8.dp),
                            color = MaterialTheme.colors.onSurface,
                        )
                    }
                },
                modifier = Modifier.padding(start = 30.dp),
                delayMillis = 200,
                tooltipPlacement = TooltipPlacement.ComponentRect(
                    alignment = Alignment.BottomCenter,
                )
            ) {
                IconButton({
                    val clipboardText = clipboardManager.getText().toString()
                    if (clipboardText.isNotEmpty()) {
                        setRemoteHandshakeLink(clipboardManager.getText().toString())
                        coroutineScope.launch {
                            scaffoldState.snackbarHostState.showSnackbar(
                                message = i18n("contact.add.remote.link_pasted_snackbar"),
                                duration = SnackbarDuration.Short,
                            )
                        }
                        aliasFocusRequester.requestFocus()
                    } else {
                        coroutineScope.launch {
                            scaffoldState.snackbarHostState.showSnackbar(
                                message = i18n("contact.add.remote.paste_error_snackbar"),
                                duration = SnackbarDuration.Short,
                            )
                        }
                    }
                }) {
                    Icon(
                        Icons.Filled.ContentPaste,
                        "contact.add.remote.paste_tooltip",
                        tint = MaterialTheme.colors.onSurface
                    )
                }
            }
        }
    )
}

@Composable
fun Alias(
    alias: String,
    setAddContactAlias: (String) -> Unit,
    aliasFocusRequester: FocusRequester,
    onSubmitAddContactDialog: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Filled.Person, "contact.add.remote.choose_nickname")
        Text(
            i18n("contact.add.remote.nickname_intro"),
            Modifier.padding(horizontal = 8.dp),
            fontSize = 14.sp,
        )
    }
    OutlinedTextField(
        alias,
        setAddContactAlias,
        label = { Text(i18n("contact.add.remote.choose_nickname")) },
        modifier = Modifier.fillMaxWidth().focusRequester(aliasFocusRequester),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        singleLine = true,
        onEnter = onSubmitAddContactDialog,
    )
}

fun errorMessage(error: AddContactError) = when (error) {
    is OwnLinkError -> Triple(ERROR, i18n("error"), i18n("contact.add.error.own_link"))
    is RemoteInvalidError -> Triple(ERROR, i18n("error"), i18n("contact.add.error.remote_invalid"))
    is AliasInvalidError -> Triple(ERROR, i18n("error"), i18n("contact.add.error.alias_invalid"))
    is LinkInvalidError -> Triple(ERROR, i18n("error"), i18nF("contact.add.error.link_invalid", error.link))
    is PublicKeyInvalidError -> Triple(
        ERROR, i18n("error"),
        i18nF("contact.add.error.public_key_invalid", error.link)
    )
    is ContactAlreadyExistsError -> {
        val intro = i18nF("contact.add.error.contact_already_exists", error.existingName)
        val explanation = i18nF("contact.add.error.duplicate_contact_explainer", error.existingName, error.alias)
        Triple(WARNING, i18n("contact.add.error.adding_failed"), (intro + "\n\n" + explanation))
    }
    is PendingAlreadyExistsError -> {
        val intro = i18nF("contact.add.error.pending_contact_already_exists", error.existingAlias)
        val explanation = i18nF("contact.add.error.duplicate_contact_explainer", error.existingAlias, error.alias)
        Triple(WARNING, i18n("contact.add.error.adding_failed"), (intro + "\n\n" + explanation))
    }
}
