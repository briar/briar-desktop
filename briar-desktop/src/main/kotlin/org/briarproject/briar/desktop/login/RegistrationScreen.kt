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
import androidx.compose.material.InitialFocusState.AFTER_FIRST_FOCUSSED
import androidx.compose.material.InitialFocusState.AFTER_FOCUS_LOST_ONCE
import androidx.compose.material.OutlinedPasswordTextField
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.briarproject.briar.desktop.login.RegistrationSubViewModel.State.CREATED
import org.briarproject.briar.desktop.login.RegistrationSubViewModel.State.CREATING
import org.briarproject.briar.desktop.login.RegistrationSubViewModel.State.INSERT_NICKNAME
import org.briarproject.briar.desktop.login.RegistrationSubViewModel.State.INSERT_PASSWORD
import org.briarproject.briar.desktop.utils.AccessibilityUtils.description
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
                    nickname = viewHolder.nickname.value,
                    setNickname = viewHolder::setNickname,
                    nicknameTooLongError = viewHolder.nicknameTooLongError.value,
                    onSubmit = viewHolder::goToPassword
                )
            }
        INSERT_PASSWORD ->
            FormScaffold(
                explanationText = i18n("startup.field.password.explanation"),
                buttonText = i18n("startup.button.register"),
                buttonClick = viewHolder::signUp,
                buttonEnabled = viewHolder.buttonEnabled.value
            ) {
                val initialFocusRequester = remember { FocusRequester() }
                val focusManager = LocalFocusManager.current
                PasswordForm(
                    focusManager = focusManager,
                    focusRequester = initialFocusRequester,
                    keyLabelPassword = "startup.field.password",
                    keyLabelPasswordConfirmation = "startup.field.password_confirmation",
                    password = viewHolder.password.value,
                    setPassword = viewHolder::setPassword,
                    passwordConfirmation = viewHolder.passwordConfirmation.value,
                    setPasswordConfirmation = viewHolder::setPasswordConfirmation,
                    passwordStrength = viewHolder.passwordStrength.value,
                    passwordTooWeakError = viewHolder.passwordTooWeakError.value,
                    passwordsDontMatchError = viewHolder.passwordMatchError.value,
                    onSubmit = viewHolder::signUp
                )

                LaunchedEffect(Unit) {
                    initialFocusRequester.requestFocus()
                }
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
    onSubmit: () -> Unit,
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
        modifier = Modifier.fillMaxWidth().focusRequester(initialFocusRequester)
            .description(i18n("startup.field.nickname")),
        onEnter = onSubmit
    )

    LaunchedEffect(Unit) {
        initialFocusRequester.requestFocus()
    }
}

/**
 * This is used here in the RegistrationScreen but also on [org.briarproject.briar.desktop.settings.ChangePasswordDialog].
 *
 * You can pass an optional [focusRequester] if the first of both password fields should request focus using a modifier.
 */
@Composable
fun PasswordForm(
    focusManager: FocusManager,
    focusRequester: FocusRequester? = null,
    keyLabelPassword: String,
    keyLabelPasswordConfirmation: String,
    password: String,
    setPassword: (String) -> Unit,
    passwordConfirmation: String,
    setPasswordConfirmation: (String) -> Unit,
    passwordStrength: Float,
    passwordTooWeakError: Boolean,
    passwordsDontMatchError: Boolean,
    onSubmit: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxWidth().requiredHeight(24.dp),
        contentAlignment = Center
    ) {
        if (password.isNotEmpty())
            StrengthMeter(passwordStrength, Modifier.fillMaxWidth())
    }
    OutlinedPasswordTextField(
        value = password,
        onValueChange = setPassword,
        label = { Text(i18n(keyLabelPassword)) },
        singleLine = true,
        isError = passwordTooWeakError,
        showErrorWhen = AFTER_FOCUS_LOST_ONCE,
        errorMessage = i18n("startup.error.password_too_weak"),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
        modifier = Modifier.fillMaxWidth().run {
            if (focusRequester != null) focusRequester(focusRequester) else this
        }.description(i18n(keyLabelPassword)),
        onEnter = { focusManager.moveFocus(FocusDirection.Next) },
    )
    OutlinedPasswordTextField(
        value = passwordConfirmation,
        onValueChange = setPasswordConfirmation,
        label = { Text(i18n(keyLabelPasswordConfirmation)) },
        singleLine = true,
        isError = passwordsDontMatchError,
        showErrorWhen = AFTER_FIRST_FOCUSSED,
        errorMessage = i18n("startup.error.passwords_not_match"),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
        modifier = Modifier.fillMaxWidth().description(i18n(keyLabelPasswordConfirmation)),
        onEnter = onSubmit,
    )
}
