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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.briarproject.briar.desktop.theme.DarkColors
import org.briarproject.briar.desktop.ui.HorizontalDivider
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n

@Preview
@Composable
fun PreviewConversationInput() {
    MaterialTheme(colors = DarkColors) {
        Surface {
            ConversationInput("Lorem ipsum.", {}, {})
        }
    }
}

@Composable
fun ConversationInput(text: String, updateText: (String) -> Unit, onSend: () -> Unit) {
    Column {
        HorizontalDivider()
        TextField(
            value = text,
            onValueChange = updateText,
            maxLines = 10,
            textStyle = TextStyle(fontSize = 16.sp, lineHeight = 16.sp),
            placeholder = { Text(i18n("conversation.message.new")) },
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
                    Icon(Icons.Filled.Add, i18n("access.attachment"), Modifier.size(24.dp), Color.White)
                }
            },
            trailingIcon = {
                IconButton(
                    onClick = onSend, modifier = Modifier.padding(4.dp).size(32.dp),
                ) {
                    Icon(
                        Icons.Filled.Send,
                        i18n("access.message.send"),
                        tint = MaterialTheme.colors.secondary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        )
    }
}
