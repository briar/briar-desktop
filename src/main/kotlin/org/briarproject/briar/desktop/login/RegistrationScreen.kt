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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.InitialFocusState.AFTER_FIRST_FOCUSSED
import androidx.compose.material.InitialFocusState.AFTER_FOCUS_LOST_ONCE
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import org.briarproject.briar.desktop.login.RegistrationSubViewModel.State.CREATED
import org.briarproject.briar.desktop.login.RegistrationSubViewModel.State.CREATING
import org.briarproject.briar.desktop.login.RegistrationSubViewModel.State.INSERT_NICKNAME
import org.briarproject.briar.desktop.login.RegistrationSubViewModel.State.INSERT_PASSWORD
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n

@Composable
fun RegistrationScreen(
    onShowAbout: () -> Unit,
    viewHolder: RegistrationSubViewModel,
) = StartupScreenScaffold(
    title = i18n("startup.title.registration"),
    onShowAbout = onShowAbout,
    showBackButton = viewHolder.showBackButton.value,
    onBackButton = viewHolder::goBack
) {
    when (viewHolder.state.value) {
        INSERT_NICKNAME ->
            FormScaffold(
                explanationText = i18n("startup.field.nickname.explanation"),
                buttonText = i18n("next"),
                buttonClick = viewHolder::goToPassword,
                buttonEnabled = viewHolder.buttonEnabled.value
            ) {
                NicknameForm(
                    viewHolder.nickname.value,
                    viewHolder::setNickname,
                    viewHolder.nicknameTooLongError.value,
                    viewHolder::goToPassword
                )
            }
        INSERT_PASSWORD ->
            FormScaffold(
                explanationText = i18n("startup.field.password.explanation"),
                buttonText = i18n("startup.button.register"),
                buttonClick = viewHolder::signUp,
                buttonEnabled = viewHolder.buttonEnabled.value
            ) {
                PasswordForm(
                    viewHolder.password.value,
                    viewHolder::setPassword,
                    viewHolder.passwordConfirmation.value,
                    viewHolder::setPasswordConfirmation,
                    viewHolder.passwordStrength.value,
                    viewHolder.passwordTooWeakError.value,
                    viewHolder.passwordMatchError.value,
                    viewHolder::signUp
                )
            }
        CREATING -> LoadingView(i18n("startup.database.creating"))
        CREATED -> {} // case handled by BriarUi
    }
}

@Composable
fun NicknameForm(
    nickname: String,
    setNickname: (String) -> Unit,
    nicknameTooLongError: Boolean,
    onEnter: () -> Unit,
) {
    val initialFocusRequester = remember { FocusRequester() }

    OutlinedTextField(
        value = nickname,
        onValueChange = setNickname,
        label = { Text(i18n("startup.field.nickname")) },
        singleLine = true,
        isError = nicknameTooLongError,
        errorMessage = i18n("startup.error.name_too_long"),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        modifier = Modifier.fillMaxWidth().focusRequester(initialFocusRequester),
        onEnter = onEnter
    )

    LaunchedEffect(Unit) {
        initialFocusRequester.requestFocus()
    }
}

@Composable
fun PasswordForm(
    password: String,
    setPassword: (String) -> Unit,
    passwordConfirmation: String,
    setPasswordConfirmation: (String) -> Unit,
    passwordStrength: Float,
    passwordTooWeakError: Boolean,
    passwordsDontMatchError: Boolean,
    onEnter: () -> Unit,
) {
    val initialFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isPasswordConfirmationVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxWidth().requiredHeight(24.dp),
        contentAlignment = Center
    ) {
        if (password.isNotEmpty())
            StrengthMeter(passwordStrength, Modifier.fillMaxWidth())
    }
    OutlinedTextField(
        value = password,
        onValueChange = setPassword,
        label = { Text(i18n("startup.field.password")) },
        singleLine = true,
        isError = passwordTooWeakError,
        showErrorWhen = AFTER_FOCUS_LOST_ONCE,
        errorMessage = i18n("startup.error.password_too_weak"),
        visualTransformation = if (!isPasswordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
        modifier = Modifier.fillMaxWidth().focusRequester(initialFocusRequester),
        onEnter = { focusManager.moveFocus(FocusDirection.Next) },
        trailingIcon = {
            ShowHidePasswordIcon(
                isVisible = isPasswordVisible,
                toggleIsVisible = {
                    isPasswordVisible = !isPasswordVisible
                },
            )
        },
        errorIcon = {
            // trailing icon is hidden in error state
            ShowHidePasswordIcon(
                isVisible = isPasswordVisible,
                toggleIsVisible = {
                    isPasswordVisible = !isPasswordVisible
                },
            )
        },
    )
    OutlinedTextField(
        value = passwordConfirmation,
        onValueChange = setPasswordConfirmation,
        label = { Text(i18n("startup.field.password_confirmation")) },
        singleLine = true,
        isError = passwordsDontMatchError,
        showErrorWhen = AFTER_FIRST_FOCUSSED,
        errorMessage = i18n("startup.error.passwords_not_match"),
        visualTransformation = if (!isPasswordConfirmationVisible) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
        modifier = Modifier.fillMaxWidth(),
        onEnter = onEnter,
        trailingIcon = {
            ShowHidePasswordIcon(
                isVisible = isPasswordConfirmationVisible,
                toggleIsVisible = {
                    isPasswordConfirmationVisible = !isPasswordConfirmationVisible
                },
            )
        },
        errorIcon = {
            // trailing icon is hidden in error state
            ShowHidePasswordIcon(
                isVisible = isPasswordConfirmationVisible,
                toggleIsVisible = {
                    isPasswordConfirmationVisible = !isPasswordConfirmationVisible
                },
            )
        }
    )

    LaunchedEffect(Unit) {
        initialFocusRequester.requestFocus()
    }
}

@Composable
private fun ShowHidePasswordIcon(
    isVisible: Boolean,
    toggleIsVisible: () -> Unit,
) {
    IconButton(
        onClick = toggleIsVisible
    ) {
        if (isVisible) {
            Icon(
                imageVector = Icons.Filled.Visibility,
                contentDescription = i18n("startup.field.password.show"),
            )
        } else {
            Icon(
                imageVector = Icons.Filled.VisibilityOff,
                contentDescription = i18n("startup.field.password.hide"),
            )
        }
    }
}