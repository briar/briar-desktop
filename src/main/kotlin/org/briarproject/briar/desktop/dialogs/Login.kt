package org.briarproject.briar.desktop.dialogs

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.svgResource
import androidx.compose.ui.unit.dp
import org.briarproject.briar.desktop.paul.theme.briarBlack

// TODO: Error handling
@Composable
fun Login(
    title: String,
    onResult: (result: String) -> Unit
) =
    // All the changes in this file are be temporary -Paul, just changing colors so I can see the button and text field
    Column(
        modifier = Modifier.padding(16.dp).fillMaxSize().background(briarBlack),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TheImage()
        Spacer(Modifier.height(32.dp))
        TheTextField(onResult)
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
private fun TheTextField(onResult: (result: String) -> Unit) {
    var password by remember { mutableStateOf("") }
    OutlinedTextField(password, { password = it }, label = { Text("Password") })
    Spacer(Modifier.height(16.dp))
    Button(
        onClick = {
            onResult.invoke(password)
        }
    ) {
        Text("Login", color = Color.Black)
    }
}
