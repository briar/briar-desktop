package org.briarproject.briar.desktop.privategroups

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.briarproject.bramble.api.contact.Contact
import org.briarproject.briar.api.privategroup.PrivateGroup
import org.briarproject.briar.desktop.contact.ContactCard
import org.briarproject.briar.desktop.contact.ContactInfoDrawerState
import org.briarproject.briar.desktop.contact.ProfileCircle
import org.briarproject.briar.desktop.introduction.IntroductionViewModel
import org.briarproject.briar.desktop.ui.Constants
import org.briarproject.briar.desktop.ui.HorizontalDivider
import org.briarproject.briar.desktop.utils.InternationalizationUtils
import org.briarproject.briar.desktop.viewmodel.viewModel
import java.util.Locale

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
fun PrivateGroupThreadDrawer(
) {
        Surface {
            Column {
                Row(Modifier.fillMaxWidth().height(Constants.HEADER_SIZE)) {
                }
                HorizontalDivider()
            }
        }
}
