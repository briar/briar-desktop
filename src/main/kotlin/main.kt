import androidx.compose.desktop.Window
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.svgResource
import androidx.compose.ui.res.vectorXmlResource
import androidx.compose.ui.unit.dp

fun main() = Window {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        TheImage()
        Spacer(Modifier.height(32.dp))
        TheText()
        TheButton()
    }
}

@Composable
private fun TheButton() {
    var text by remember { mutableStateOf("Start chatting") }
    Button(onClick = {
        text = "Sorry, not yet available"
    }) {
        Text(text)
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
private fun TheText() {
    Text("Welcome to Briar")
}