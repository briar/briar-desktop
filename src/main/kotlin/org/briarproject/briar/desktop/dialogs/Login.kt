package org.briarproject.briar.desktop.dialogs

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
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.res.svgResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

// TODO: Error handling
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Login(
    modifier: Modifier = Modifier,
    onResult: (result: String) -> Unit
) {
    var password by remember { mutableStateOf("") }
    val initialFocusRequester = remember { FocusRequester() }
    Column(
        modifier = modifier.padding(16.dp).fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BriarLogo()
        Spacer(Modifier.height(32.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            textStyle = TextStyle(color = Color.White),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { onResult.invoke(password) }),
            modifier = Modifier
                .focusRequester(initialFocusRequester)
                .onPreviewKeyEvent {
                    if (it.type == KeyEventType.KeyUp && it.key == Key.Enter) {
                        onResult.invoke(password)
                    }
                    false
                },
        )
        Spacer(Modifier.height(16.dp))
        Button(onClick = { onResult.invoke(password) }) {
            Text("Login", color = Color.Black)
        }

        DisposableEffect(Unit) {
            initialFocusRequester.requestFocus()
            onDispose { }
        }
    }
}

@Composable
fun BriarLogo(modifier: Modifier = Modifier.fillMaxWidth().clip(shape = RoundedCornerShape(400.dp))) =
    Image(svgResource("images/logo_circle.svg"), "Briar logo", modifier)
