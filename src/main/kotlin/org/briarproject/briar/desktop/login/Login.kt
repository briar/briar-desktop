package org.briarproject.briar.desktop.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n

// TODO: Error handling
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Login(
    viewModel: LoginViewModel,
    modifier: Modifier = Modifier,
    onSignedIn: () -> Unit
) {
    val signIn = {
        viewModel.signIn {
            onSignedIn()
        }
    }

    val initialFocusRequester = remember { FocusRequester() }
    Surface {
        Column(
            modifier = modifier.padding(16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BriarLogo()
            Spacer(Modifier.height(32.dp))
            OutlinedTextField(
                value = viewModel.password.value,
                onValueChange = viewModel::setPassword,
                label = { Text(i18n("password")) },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { signIn() }),
                modifier = Modifier
                    .focusRequester(initialFocusRequester)
                    .onPreviewKeyEvent {
                        if (it.type == KeyEventType.KeyUp && it.key == Key.Enter) {
                            signIn()
                        }
                        false
                    },
            )
            Spacer(Modifier.height(16.dp))
            Button(onClick = { signIn() }) {
                Text(i18n("login.login"))
            }

            DisposableEffect(Unit) {
                initialFocusRequester.requestFocus()
                onDispose { }
            }
        }
    }
}

@Composable
fun BriarLogo(modifier: Modifier = Modifier.fillMaxWidth().clip(shape = RoundedCornerShape(400.dp))) =
    Image(painterResource("images/logo_circle.svg"), i18n("access.logo"), modifier)
