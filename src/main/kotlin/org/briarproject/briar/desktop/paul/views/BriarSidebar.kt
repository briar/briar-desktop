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
fun BriarSidebar(UIMode: String, onModeChange: (String) -> Unit) {
    Surface(modifier = Modifier.width(66.dp).fillMaxHeight(), color = briarBlue) {
        Column(verticalArrangement = Arrangement.Top) {
            IconButton(
                modifier = Modifier.align(Alignment.CenterHorizontally)
                    .padding(top = 9.dp, bottom = 10.dp),
                onClick = {}
            ) {
                Image(
                    bitmap = imageFromResource("images/profile_images/p0.png"),
                    "my_profile_image",
                    modifier = Modifier.size(48.dp).align(Alignment.CenterHorizontally).clip(
                        CircleShape
                    ).border(2.dp, color = Color.White, CircleShape)
                )
            }
            BriarSidebarButton(
                UIMode = UIMode,
                onModeChange = onModeChange,
                "Contacts",
                Icons.Filled.Contacts
            )
            BriarSidebarButton(
                UIMode = UIMode,
                onModeChange = onModeChange,
                "Private Groups",
                Icons.Filled.Group
            )
            BriarSidebarButton(
                UIMode = UIMode,
                onModeChange = onModeChange,
                "Forums",
                Icons.Filled.Forum
            )
            BriarSidebarButton(
                UIMode = UIMode,
                onModeChange = onModeChange,
                "Blogs",
                Icons.Filled.ChromeReaderMode
            )
        }
        Column(verticalArrangement = Arrangement.Bottom) {
            BriarSidebarButton(
                UIMode = UIMode,
                onModeChange = onModeChange,
                "Transports",
                Icons.Filled.WifiTethering
            )
            BriarSidebarButton(
                UIMode = UIMode,
                onModeChange = onModeChange,
                "Settings",
                Icons.Filled.Settings
            )
            BriarSidebarButton(
                UIMode = UIMode,
                onModeChange = onModeChange,
                "Sign Out",
                Icons.Filled.Logout
            )
        }
    }
}

@Composable
fun BriarSidebarButton(
    UIMode: String,
    onModeChange: (String) -> Unit,
    thisMode: String,
    icon: ImageVector
) {
    val bg = if (UIMode == thisMode) briarBlack else briarBlue
    Column() {
        IconButton(
            modifier = Modifier.align(Alignment.CenterHorizontally).background(color = bg)
                .padding(vertical = 9.dp, horizontal = 12.dp),
            onClick = { onModeChange(thisMode) }
        ) {
            Icon(icon, thisMode, tint = Color.White, modifier = Modifier.size(30.dp))
        }
    }
}
