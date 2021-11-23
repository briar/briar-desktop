package org.briarproject.briar.desktop.privategroups

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.briarproject.briar.desktop.ui.Constants
import org.briarproject.briar.desktop.ui.HorizontalDivider

// Right drawer state
enum class PrivateGroupDrawerState {
    VIEW_THREAD,
    MEMBER_LIST,
    REVEAL_CONTACT
}

@Composable
fun PrivateGroupDrawer(
    drawerState: PrivateGroupDrawerState,
) {
    when (drawerState) {
        PrivateGroupDrawerState.VIEW_THREAD -> PrivateGroupThreadDrawer()
    }
}

@Composable
fun PrivateGroupThreadDrawer() {
    Surface {
        Column {
            Row(Modifier.fillMaxWidth().height(Constants.HEADER_SIZE)) {
            }
            HorizontalDivider()
        }
    }
}
