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
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

// TODO: Error handling
@Composable
fun Login(
    title: String,
    modifier: Modifier = Modifier,
    onResult: (result: String) -> Unit
) =
    Column(
        modifier = modifier.padding(16.dp).fillMaxSize(),
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
    OutlinedTextField(password, { password = it }, label = { Text("Password") }, textStyle = TextStyle(color = MaterialTheme.colors.onBackground))
    Spacer(Modifier.height(16.dp))
    Button(
        onClick = {
            onResult.invoke(password)
        }
    ) {
        Text("Login")
    }
}
