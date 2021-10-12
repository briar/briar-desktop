package org.briarproject.briar.desktop.conversation

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Send
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.briarproject.briar.desktop.theme.DarkColors

@Preview
@Composable
fun PreviewConversationInput() {
    MaterialTheme(colors = DarkColors) {
        Surface {
            ConversationInput()
        }
    }
}

@Composable
fun ConversationInput() {
    var text by remember { mutableStateOf("") }
    Column {
        HorizontalDivider()
        TextField(
            value = text,
            onValueChange = { text = it },
            maxLines = 10,
            textStyle = TextStyle(fontSize = 16.sp, lineHeight = 16.sp),
            placeholder = { Text("Message") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(0.dp),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = MaterialTheme.colors.background,
                focusedIndicatorColor = MaterialTheme.colors.background,
                unfocusedIndicatorColor = MaterialTheme.colors.background
            ),
            leadingIcon = {
                IconButton(
                    onClick = {},
                    Modifier.padding(4.dp).size(32.dp)
                        .background(MaterialTheme.colors.primary, CircleShape),
                ) {
                    Icon(Icons.Filled.Add, "add attachment", Modifier.size(24.dp), Color.White)
                }
            },
            trailingIcon = {
                IconButton(
                    onClick = { }, modifier = Modifier.padding(4.dp).size(32.dp),
                ) {
                    Icon(
                        Icons.Filled.Send,
                        "send message",
                        tint = MaterialTheme.colors.secondary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        )
    }
}
