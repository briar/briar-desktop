package org.briarproject.briar.desktop.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n
import java.util.Locale

// TODO: Error handling and password strength
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Registration(
    viewModel: RegistrationViewModel,
    modifier: Modifier = Modifier,
    onSignedUp: () -> Unit
) {
    val signUp = {
        viewModel.signUp {
            onSignedUp()
        }
    }

    val initialFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    Surface {
        Column(
            modifier = modifier.padding(16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BriarLogo()
            Spacer(Modifier.height(32.dp))
            OutlinedTextField(
                value = viewModel.username.value,
                onValueChange = { viewModel.setUsername(it) },
                label = { Text(i18n("registration.username")) },
                singleLine = true,
                textStyle = TextStyle(color = Color.White),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Next) }),
                modifier = Modifier
                    .focusRequester(initialFocusRequester)
                    .onPreviewKeyEvent {
                        if (it.type == KeyEventType.KeyUp && it.key == Key.Enter) {
                            focusManager.moveFocus(FocusDirection.Next)
                        }
                        false
                    },
            )
            OutlinedTextField(
                value = viewModel.password.value,
                onValueChange = { viewModel.setPassword(it) },
                label = { Text(i18n("password")) },
                singleLine = true,
                textStyle = TextStyle(color = Color.White),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { signUp() }),
                modifier = Modifier.onPreviewKeyEvent {
                    if (it.type == KeyEventType.KeyUp && it.key == Key.Enter) {
                        signUp()
                    }
                    false
                },
            )
            Spacer(Modifier.height(16.dp))
            Button(onClick = { signUp() }) {
                val text = i18n("registration.register")
                Text(text.uppercase(Locale.getDefault()), color = Color.Black)
            }

            DisposableEffect(Unit) {
                initialFocusRequester.requestFocus()
                onDispose { }
            }
        }
    }
}
