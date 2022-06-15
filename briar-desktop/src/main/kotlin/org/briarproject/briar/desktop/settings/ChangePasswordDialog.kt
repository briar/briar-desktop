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

package org.briarproject.briar.desktop.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.ButtonType
import androidx.compose.material.DialogButton
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.InitialFocusState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedPasswordTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import org.briarproject.bramble.api.crypto.DecryptionResult
import org.briarproject.bramble.api.crypto.DecryptionResult.INVALID_PASSWORD
import org.briarproject.briar.desktop.login.PasswordForm
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n
import org.briarproject.briar.desktop.utils.PreviewUtils.preview
import java.lang.Float.min

@Suppress("HardCodedStringLiteral")
fun main() = preview(
    "visible" to true,
) {
    val (oldPassword, setOldPassword) = remember { mutableStateOf("") }
    val (password, setPassword) = remember { mutableStateOf("") }
    val (passwordConfirmation, setPasswordConfirmation) = remember { mutableStateOf("") }
    val passwordStrength = derivedStateOf {
        min(password.length / 10f, 1f)
    }
    val passwordTooWeakError = derivedStateOf {
        passwordStrength.value < 0.6f
    }
    val (submitError, setSubmitError) = remember { mutableStateOf<DecryptionResult?>(null) }
    ChangePasswordDialog(
        isVisible = getBooleanParameter("visible"),
        close = { setBooleanParameter("visible", false) },
        onChange = {
            // use password with 8 or less to test failure
            if (password.length <= 8) {
                setSubmitError(INVALID_PASSWORD)
                false
            } else {
                true
            }
        },
        oldPassword = oldPassword,
        setOldPassword = setOldPassword,
        password = password,
        setPassword = setPassword,
        passwordConfirmation = passwordConfirmation,
        setPasswordConfirmation = setPasswordConfirmation,
        passwordStrength = passwordStrength.value,
        passwordTooWeakError = passwordTooWeakError.value,
        passwordsDontMatchError = password != passwordConfirmation,
        buttonEnabled = !passwordTooWeakError.value && password == passwordConfirmation,
        submitError = submitError,
    )
}

@Composable
fun ChangePasswordDialog(
    isVisible: Boolean,
    close: () -> Unit,
    viewHolder: ChangePasswordSubViewModel,
) {
    ChangePasswordDialog(
        isVisible = isVisible,
        close = close,
        onChange = viewHolder::confirmChange,
        oldPassword = viewHolder.oldPassword.value,
        setOldPassword = viewHolder::setOldPassword,
        password = viewHolder.password.value,
        setPassword = viewHolder::setPassword,
        passwordConfirmation = viewHolder.passwordConfirmation.value,
        setPasswordConfirmation = viewHolder::setPasswordConfirmation,
        passwordStrength = viewHolder.passwordStrength.value,
        passwordTooWeakError = viewHolder.passwordTooWeakError.value,
        passwordsDontMatchError = viewHolder.passwordMatchError.value,
        buttonEnabled = viewHolder.buttonEnabled.value,
        submitError = viewHolder.submitError.value,
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ChangePasswordDialog(
    isVisible: Boolean,
    close: () -> Unit,
    onChange: () -> Boolean,
    oldPassword: String,
    setOldPassword: (String) -> Unit,
    password: String,
    setPassword: (String) -> Unit,
    passwordConfirmation: String,
    setPasswordConfirmation: (String) -> Unit,
    passwordStrength: Float,
    passwordTooWeakError: Boolean,
    passwordsDontMatchError: Boolean,
    buttonEnabled: Boolean,
    submitError: DecryptionResult?,
) {
    if (!isVisible) return

    val onClose = {
        setOldPassword("")
        setPassword("")
        setPasswordConfirmation("")
        close()
    }

    val onSubmit = {
        if (onChange()) {
            onClose()
        }
    }

    AlertDialog(
        onDismissRequest = onClose,
        title = {
            Text(
                text = i18n("settings.security.password.change"),
                modifier = Modifier.width(IntrinsicSize.Max),
                style = MaterialTheme.typography.h6,
            )
        },
        text = {
            PasswordForm(
                oldPassword = oldPassword,
                setOldPassword = setOldPassword,
                password = password,
                setPassword = setPassword,
                passwordConfirmation = passwordConfirmation,
                setPasswordConfirmation = setPasswordConfirmation,
                passwordStrength = passwordStrength,
                passwordTooWeakError = passwordTooWeakError,
                passwordsDontMatchError = passwordsDontMatchError,
                onSubmit = onSubmit,
                submitError = submitError,
            )
        },
        dismissButton = {
            DialogButton(
                onClick = onClose,
                text = i18n("cancel"),
                type = ButtonType.NEUTRAL,
            )
        },
        confirmButton = {
            DialogButton(
                onClick = onSubmit,
                text = i18n("change"),
                type = ButtonType.NEUTRAL,
                enabled = buttonEnabled,
            )
        },
    )
}

@Composable
fun PasswordForm(
    oldPassword: String,
    setOldPassword: (String) -> Unit,
    password: String,
    setPassword: (String) -> Unit,
    passwordConfirmation: String,
    setPasswordConfirmation: (String) -> Unit,
    passwordStrength: Float,
    passwordTooWeakError: Boolean,
    passwordsDontMatchError: Boolean,
    onSubmit: () -> Unit,
    submitError: DecryptionResult?,
) {
    val initialFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    Column {
        OutlinedPasswordTextField(
            value = oldPassword,
            onValueChange = setOldPassword,
            label = { Text(i18n("settings.security.password.current")) },
            singleLine = true,
            isError = submitError == INVALID_PASSWORD,
            showErrorWhen = InitialFocusState.FROM_START,
            errorMessage = i18n("startup.error.password_wrong"),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth().focusRequester(initialFocusRequester),
            onEnter = { focusManager.moveFocus(FocusDirection.Next) },
        )
        PasswordForm(
            focusManager = focusManager,
            keyLabelPassword = "settings.security.password.choose",
            keyLabelPasswordConfirmation = "settings.security.password.confirm",
            password = password,
            setPassword = setPassword,
            passwordConfirmation = passwordConfirmation,
            setPasswordConfirmation = setPasswordConfirmation,
            passwordStrength = passwordStrength,
            passwordTooWeakError = passwordTooWeakError,
            passwordsDontMatchError = passwordsDontMatchError,
            onSubmit = onSubmit,
        )
    }

    LaunchedEffect(Unit) {
        initialFocusRequester.requestFocus()
    }
}
