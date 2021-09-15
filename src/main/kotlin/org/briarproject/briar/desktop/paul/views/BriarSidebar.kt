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
fun BriarSidebar(uiMode: String, setMode: (String) -> Unit) {
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
                setMode = setMode,
                "Contacts",
                Icons.Filled.Contacts
            )
            BriarSidebarButton(
                uiMode = uiMode,
                setMode = setMode,
                "Private Groups",
                Icons.Filled.Group
            )
            BriarSidebarButton(
                uiMode = uiMode,
                setMode = setMode,
                "Forums",
                Icons.Filled.Forum
            )
            BriarSidebarButton(
                uiMode = uiMode,
                setMode = setMode,
                "Blogs",
                Icons.Filled.ChromeReaderMode
            )
        }
        Column(verticalArrangement = Arrangement.Bottom) {
            BriarSidebarButton(
                uiMode = uiMode,
                setMode = setMode,
                "Transports",
                Icons.Filled.WifiTethering
            )
            BriarSidebarButton(
                uiMode = uiMode,
                setMode = setMode,
                "Settings",
                Icons.Filled.Settings
            )
            BriarSidebarButton(
                uiMode = uiMode,
                setMode = setMode,
                "Sign Out",
                Icons.Filled.Logout
            )
        }
    }
}

@Composable
fun BriarSidebarButton(uiMode: String, setMode: (String) -> Unit, thisMode: String, icon: ImageVector) {
    val bg = if (uiMode == thisMode) briarBlack else briarBlue
    Column {
        IconButton(
            modifier = Modifier.align(Alignment.CenterHorizontally).background(color = bg)
                .padding(vertical = 4.dp, horizontal = 12.dp),
            onClick = { setMode(thisMode) }
        ) {
            Icon(icon, thisMode, tint = Color.White, modifier = Modifier.size(30.dp))
        }
    }
}
