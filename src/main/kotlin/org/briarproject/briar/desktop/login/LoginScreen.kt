package org.briarproject.briar.desktop.login

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import org.briarproject.briar.desktop.login.LoginViewModel.State.COMPACTING
import org.briarproject.briar.desktop.login.LoginViewModel.State.MIGRATING
import org.briarproject.briar.desktop.login.LoginViewModel.State.SIGNED_OUT
import org.briarproject.briar.desktop.login.LoginViewModel.State.STARTED
import org.briarproject.briar.desktop.login.LoginViewModel.State.STARTING
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n
import org.briarproject.briar.desktop.viewmodel.viewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
) = StartupScreen(i18n("startup.title.login")) { // todo: i18n
    when (viewModel.state.value) {
        SIGNED_OUT ->
            FormScaffold(
                explanationText = null,
                buttonText = i18n("startup.button.login"),
                buttonClick = viewModel::signIn,
                buttonEnabled = viewModel.buttonEnabled.value
            ) {
                LoginForm(
                    viewModel.password.value,
                    viewModel::setPassword,
                    viewModel.passwordInvalidError.value,
                    viewModel::signIn
                )
            }
        STARTING -> LoadingView(i18n("startup.database.opening"))
        MIGRATING -> LoadingView(i18n("startup.database.migrating"))
        COMPACTING -> LoadingView(i18n("startup.database.compacting"))
        STARTED -> {} // case handled by BriarUi
    }

    if (viewModel.decryptionFailedError.value) {
        // todo: is this actually needed on Desktop?
        AlertDialog(
            onDismissRequest = viewModel::closeDecryptionFailedDialog,
            title = { Text(i18n("startup.error.decryption.title")) },
            text = { Text(i18n("startup.error.decryption.text")) },
            confirmButton = {
                Button(onClick = viewModel::closeDecryptionFailedDialog) {
                    Text(i18n("ok"))
                }
            },
        )
    }
}

@Composable
fun LoginForm(
    password: String,
    setPassword: (String) -> Unit,
    passwordInvalidError: Boolean,
    onEnter: () -> Unit,
) {
    val initialFocusRequester = remember { FocusRequester() }

    OutlinedTextField(
        value = password,
        onValueChange = setPassword,
        label = { Text(i18n("startup.field.password")) },
        singleLine = true,
        isError = passwordInvalidError,
        errorMessage = i18n("startup.error.password_wrong"),
        textStyle = TextStyle(color = Color.White),
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next),
        modifier = Modifier.fillMaxWidth().focusRequester(initialFocusRequester),
        onEnter = onEnter
    )

    LaunchedEffect(Unit) {
        initialFocusRequester.requestFocus()
    }
}
