package org.briarproject.briar.desktop.login

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.InitialFocusState.AFTER_FIRST_FOCUSSED
import androidx.compose.material.InitialFocusState.AFTER_FOCUS_LOST_ONCE
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import org.briarproject.briar.desktop.login.RegistrationViewModel.State.CREATED
import org.briarproject.briar.desktop.login.RegistrationViewModel.State.CREATING
import org.briarproject.briar.desktop.login.RegistrationViewModel.State.INSERT_NICKNAME
import org.briarproject.briar.desktop.login.RegistrationViewModel.State.INSERT_PASSWORD
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n
import org.briarproject.briar.desktop.viewmodel.viewModel

@Composable
fun RegistrationScreen(
    viewModel: RegistrationViewModel = viewModel(),
) = StartupScreen(
    title = i18n("startup.title.registration"),
    showBackButton = viewModel.showBackButton.value,
    onBackButton = viewModel::goBack
) {
    when (viewModel.state.value) {
        INSERT_NICKNAME ->
            FormScaffold(
                explanationText = i18n("startup.field.nickname.explanation"),
                buttonText = i18n("next"),
                buttonClick = viewModel::goToPassword,
                buttonEnabled = viewModel.buttonEnabled.value
            ) {
                NicknameForm(
                    viewModel.nickname.value,
                    viewModel::setNickname,
                    viewModel.nicknameTooLongError.value,
                    viewModel::goToPassword
                )
            }
        INSERT_PASSWORD ->
            FormScaffold(
                explanationText = i18n("startup.field.password.explanation"),
                buttonText = i18n("startup.button.register"),
                buttonClick = viewModel::signUp,
                buttonEnabled = viewModel.buttonEnabled.value
            ) {
                PasswordForm(
                    viewModel.password.value,
                    viewModel::setPassword,
                    viewModel.passwordConfirmation.value,
                    viewModel::setPasswordConfirmation,
                    viewModel.passwordStrength.value,
                    viewModel.passwordTooWeakError.value,
                    viewModel.passwordMatchError.value,
                    viewModel::signUp
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
        textStyle = TextStyle(color = Color.White),
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
        textStyle = TextStyle(color = Color.White),
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
        modifier = Modifier.fillMaxWidth().focusRequester(initialFocusRequester),
        onEnter = { focusManager.moveFocus(FocusDirection.Next) }
    )
    OutlinedTextField(
        value = passwordConfirmation,
        onValueChange = setPasswordConfirmation,
        label = { Text(i18n("startup.field.password_confirmation")) },
        singleLine = true,
        isError = passwordsDontMatchError,
        showErrorWhen = AFTER_FIRST_FOCUSSED,
        errorMessage = i18n("startup.error.passwords_not_match"),
        textStyle = TextStyle(color = Color.White),
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
        modifier = Modifier.fillMaxWidth(),
        onEnter = onEnter,
    )

    LaunchedEffect(Unit) {
        initialFocusRequester.requestFocus()
    }
}
