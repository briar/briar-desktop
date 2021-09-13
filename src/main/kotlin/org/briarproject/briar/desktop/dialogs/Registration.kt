package org.briarproject.briar.desktop.dialogs

import androidx.compose.desktop.Window
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.svgResource
import androidx.compose.ui.unit.dp
import org.briarproject.bramble.api.account.AccountManager
import org.briarproject.bramble.api.lifecycle.LifecycleManager

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
    OutlinedTextField(username, { username = it }, label = { Text("Username") })
    OutlinedTextField(password, { password = it }, label = { Text("Password") })
    Spacer(Modifier.height(16.dp))
    Button(
        onClick = {
            accountManager.createAccount(username, password)
            val dbKey = accountManager.databaseKey ?: throw AssertionError()
            lifecycleManager.startServices(dbKey)
            lifecycleManager.waitForStartup()
        }
    ) {
        Text("Register")
    }
}
