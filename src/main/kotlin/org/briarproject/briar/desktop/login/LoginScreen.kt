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

package org.briarproject.briar.desktop.login

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import org.briarproject.briar.desktop.login.LoginSubViewModel.State.COMPACTING
import org.briarproject.briar.desktop.login.LoginSubViewModel.State.MIGRATING
import org.briarproject.briar.desktop.login.LoginSubViewModel.State.SIGNED_OUT
import org.briarproject.briar.desktop.login.LoginSubViewModel.State.STARTED
import org.briarproject.briar.desktop.login.LoginSubViewModel.State.STARTING
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LoginScreen(
    onShowAbout: () -> Unit,
    viewHolder: LoginSubViewModel,
) = StartupScreenScaffold(
    title = i18n("startup.title.login"),
    onShowAbout = onShowAbout,
) {
    when (viewHolder.state.value) {
        SIGNED_OUT ->
            FormScaffold(
                explanationText = null,
                buttonText = i18n("startup.button.login"),
                buttonClick = viewHolder::signIn,
                buttonEnabled = viewHolder.buttonEnabled.value
            ) {
                LoginForm(
                    viewHolder.password.value,
                    viewHolder::setPassword,
                    viewHolder.passwordInvalidError.value,
                    viewHolder::deleteAccount,
                    viewHolder::signIn
                )
            }
        STARTING -> LoadingView(i18n("startup.database.opening"))
        MIGRATING -> LoadingView(i18n("startup.database.migrating"))
        COMPACTING -> LoadingView(i18n("startup.database.compacting"))
        STARTED -> {} // case handled by BriarUi
    }

    if (viewHolder.decryptionFailedError.value) {
        // todo: this should never be triggered,
        //  since we don't use any keyStrengthener for now
        //  when adding this, we could think about showing
        //  a proper error screen instead
        AlertDialog(
            onDismissRequest = viewHolder::closeDecryptionFailedDialog,
            title = { Text(i18n("startup.error.decryption.title")) },
            text = { Text(i18n("startup.error.decryption.text")) },
            confirmButton = {
                TextButton(onClick = viewHolder::closeDecryptionFailedDialog) {
                    Text(i18n("ok"))
                }
            },
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LoginForm(
    password: String,
    setPassword: (String) -> Unit,
    passwordInvalidError: Boolean,
    deleteAccount: () -> Unit,
    onEnter: () -> Unit,
) {
    val initialFocusRequester = remember { FocusRequester() }
    val passwordForgotten = remember { mutableStateOf(false) }

    OutlinedTextField(
        value = password,
        onValueChange = setPassword,
        label = { Text(i18n("startup.field.password")) },
        singleLine = true,
        isError = passwordInvalidError,
        errorMessage = i18n("startup.error.password_wrong"),
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
        modifier = Modifier.fillMaxWidth().focusRequester(initialFocusRequester),
        onEnter = onEnter
    )
    TextButton(onClick = { passwordForgotten.value = true }) {
        Text(i18n("startup.password_forgotten.button"))
    }

    if (passwordForgotten.value) {
        val closeDialog = { passwordForgotten.value = false }
        AlertDialog(
            onDismissRequest = closeDialog,
            title = { Text(i18n("startup.password_forgotten.title")) },
            text = { Text(i18n("startup.password_forgotten.text")) },
            confirmButton = {
                TextButton(onClick = closeDialog) {
                    Text(i18n("cancel"))
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteAccount(); closeDialog() }) {
                    Text(i18n("delete"))
                }
            },
            modifier = Modifier.width(500.dp)
        )
    }

    LaunchedEffect(Unit) {
        initialFocusRequester.requestFocus()
    }
}
