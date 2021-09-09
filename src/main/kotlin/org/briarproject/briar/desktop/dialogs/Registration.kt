package org.briarproject.briar.desktop.dialogs

import androidx.compose.desktop.Window
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.svgResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import org.briarproject.bramble.api.account.AccountManager
import org.briarproject.bramble.api.lifecycle.LifecycleManager
import java.awt.event.KeyEvent

// TODO: Error handling and password strength
fun Registration(title: String, accountManager: AccountManager, lifecycleManager: LifecycleManager) =
    Window(title = title) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TheImage()
            Spacer(Modifier.height(32.dp))
            TheTextFields(accountManager, lifecycleManager)
        }
    }

@Composable
private fun TheImage() {
    Image(
        painter = svgResource("images/logo_circle.svg"),
        contentDescription = "Briar logo",
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = RoundedCornerShape(400.dp))
    )
}

@Composable
private fun TheTextFields(accountManager: AccountManager, lifecycleManager: LifecycleManager) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val focusManager = LocalFocusManager.current

    val register = {
        accountManager.createAccount(username, password)

        val dbKey = accountManager.databaseKey ?: throw AssertionError()
        lifecycleManager.startServices(dbKey)
        lifecycleManager.waitForStartup()
    }

    OutlinedTextField(
        value = username,
        onValueChange = { username = it },
        label = { Text("Username") },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
        modifier = Modifier.onKeyEvent {
            if (it.nativeKeyEvent.keyCode == KeyEvent.VK_TAB) {
                focusManager.moveFocus(FocusDirection.Down)
                return@onKeyEvent true
            }
            false
        }
    )
    OutlinedTextField(
        value = password,
        onValueChange = { password = it },
        label = { Text("Password") },
        singleLine = true,
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { register() }),
        modifier = Modifier.onKeyEvent {
            if (it.nativeKeyEvent.keyCode == KeyEvent.VK_ENTER) {
                register()
                return@onKeyEvent true
            }
            false
        }
    )
    Spacer(Modifier.height(16.dp))
    Button(onClick = register) {
        Text("Register")
    }
}
