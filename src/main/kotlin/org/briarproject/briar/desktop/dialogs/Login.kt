package org.briarproject.briar.desktop.dialogs

import androidx.compose.desktop.Window
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.svgResource
import androidx.compose.ui.unit.dp
import org.briarproject.bramble.api.account.AccountManager
import org.briarproject.bramble.api.lifecycle.LifecycleManager

// TODO: Error handling
fun Login(title: String, accountManager: AccountManager, lifecycleManager: LifecycleManager) =
    Window(title = title) {
        Column(
            modifier = Modifier.padding(16.dp).fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TheImage()
            Spacer(Modifier.height(32.dp))
            TheTextField(accountManager, lifecycleManager)
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
private fun TheTextField(accountManager: AccountManager, lifecycleManager: LifecycleManager) {
    var password by remember { mutableStateOf("") }
    OutlinedTextField(password, { password = it }, label = { Text("Password") })
    Spacer(Modifier.height(16.dp))
    Button(onClick = {
        accountManager.signIn(password)

        val dbKey = accountManager.databaseKey ?: throw AssertionError()
        lifecycleManager.startServices(dbKey)
        lifecycleManager.waitForStartup()
    }) {
        Text("Login")
    }
}