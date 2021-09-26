package org.briarproject.briar.desktop.paul.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChromeReaderMode
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WifiTethering
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.imageFromResource
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import org.briarproject.briar.desktop.paul.theme.briarBlack
import org.briarproject.briar.desktop.paul.theme.briarBlue

@Composable
fun BriarSidebar(uiMode: UiModes, setUiMode: (UiModes) -> Unit) {
    Surface(modifier = Modifier.width(56.dp).fillMaxHeight(), color = briarBlue) {
        Column(verticalArrangement = Arrangement.Top) {
            IconButton(
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 5.dp, bottom = 4.dp),
                onClick = {}
            ) {
                Image(
                    bitmap = imageFromResource("images/profile_images/p0.png"),
                    "my_profile_image",
                    modifier = Modifier.size(44.dp).align(Alignment.CenterHorizontally).clip(
                        CircleShape
                    ).border(2.dp, color = Color.White, CircleShape)
                )
            }
            BriarSidebarButton(
                uiMode = uiMode,
                setUiMode = setUiMode,
                UiModes.CONTACTS,
                Icons.Filled.Contacts
            )
            BriarSidebarButton(
                uiMode = uiMode,
                setUiMode = setUiMode,
                UiModes.GROUPS,
                Icons.Filled.Group
            )
            BriarSidebarButton(
                uiMode = uiMode,
                setUiMode = setUiMode,
                UiModes.FORUMS,
                Icons.Filled.Forum
            )
            BriarSidebarButton(
                uiMode = uiMode,
                setUiMode = setUiMode,
                UiModes.BLOGS,
                Icons.Filled.ChromeReaderMode
            )
        }
        Column(verticalArrangement = Arrangement.Bottom) {
            BriarSidebarButton(
                uiMode = uiMode,
                setUiMode = setUiMode,
                UiModes.TRANSPORTS,
                Icons.Filled.WifiTethering
            )
            BriarSidebarButton(
                uiMode = uiMode,
                setUiMode = setUiMode,
                UiModes.SETTINGS,
                Icons.Filled.Settings
            )
            BriarSidebarButton(
                uiMode = uiMode,
                setUiMode = setUiMode,
                UiModes.SIGNOUT,
                Icons.Filled.Logout
            )
        }
    }
}

@Composable
fun BriarSidebarButton(uiMode: UiModes, setUiMode: (UiModes) -> Unit, thisMode: UiModes, icon: ImageVector) {
    val tint = if (uiMode == thisMode) MaterialTheme.colors.primary else Color.White
    Column {
        IconButton(
            modifier = Modifier.align(Alignment.CenterHorizontally)
                .padding(vertical = 4.dp, horizontal = 12.dp),
            onClick = { setUiMode(thisMode) }
        ) {
            Icon(icon, thisMode.toString(), tint = tint, modifier = Modifier.size(30.dp))
        }
    }
}
