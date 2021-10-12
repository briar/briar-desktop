package org.briarproject.briar.desktop.conversation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SearchTextField(searchValue: String, onValueChange: (String) -> Unit, onContactAdd: (Boolean) -> Unit) {
    TextField(
        value = searchValue,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = TextStyle(fontSize = 16.sp, color = MaterialTheme.colors.onSurface),
        placeholder = { Text("Contacts") },
        shape = RoundedCornerShape(0.dp),
        leadingIcon = {
            val padding = Modifier.padding(top = 8.dp, bottom = 8.dp, start = 12.dp)
            Icon(Icons.Filled.Search, "search contacts", padding)
        },
        trailingIcon = {
            IconButton(
                onClick = { onContactAdd(true) },
                modifier = Modifier.padding(end = 10.dp).size(32.dp)
                    .background(MaterialTheme.colors.primary, CircleShape)
            ) {
                Icon(Icons.Filled.PersonAdd, "add contact", tint = Color.White, modifier = Modifier.size(20.dp))
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}
