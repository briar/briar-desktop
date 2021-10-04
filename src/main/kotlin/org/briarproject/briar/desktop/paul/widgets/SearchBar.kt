package org.briarproject.briar.desktop.paul.widgets

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.briarproject.briar.desktop.paul.theme.divider
import org.briarproject.briar.desktop.paul.theme.placeholder
import org.briarproject.briar.desktop.paul.theme.sidebarSurface
import org.briarproject.briar.desktop.paul.theme.surfaceVariant
import org.briarproject.briar.desktop.paul.views.HEADER_SIZE

@Composable
fun SearchTextField(
    modifier: Modifier,
    searchValue: String,
    onValueChange: (String) -> Unit,
    onContactAdd: (Boolean) -> Unit,
    focus: Boolean,
    setFocus: (Boolean) -> Unit
) {
    TextField(
        value = searchValue,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = TextStyle(fontSize = 14.sp),
        placeholder = { Text("Contacts") },
        shape = RoundedCornerShape(0.dp),
        leadingIcon = {
            val padding = Modifier.padding(top = 8.dp, bottom = 8.dp, start = 4.dp)
            Icon(
                Icons.Filled.ArrowBack,
                "search contacts",
                padding.size(28.dp).clickable { setFocus(false); onValueChange("") })
        },
        trailingIcon = {
        },
        modifier = modifier
    )
}

@Composable
fun SearchCard(focus: Boolean, setFocus: (Boolean) -> Unit, focusRequester: FocusRequester) {
    Row(
        Modifier.fillMaxSize().background(MaterialTheme.colors.surfaceVariant),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Surface(
            modifier = Modifier.padding(start = 16.dp).fillMaxWidth(0.77f)
                .clickable { setFocus(true); focusRequester.requestFocus() }
                .border(1.dp, MaterialTheme.colors.divider, RoundedCornerShape(8.dp)),
            color = MaterialTheme.colors.sidebarSurface,
            shape = RoundedCornerShape(8.dp),
            elevation = 1.dp
        ) {
            Row(Modifier.padding(horizontal = 4.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                val padding = Modifier.padding(start = 8.dp, end = 8.dp)
                Icon(Icons.Filled.Search, "search contacts", padding)
                Text("Contacts", color = MaterialTheme.colors.placeholder)
            }
        }
        IconButton(
            onClick = { println("ahhhhhh") },
            modifier = Modifier.padding(start = 8.dp, end = 16.dp).size(32.dp).background(
                MaterialTheme.colors.primary, CircleShape
            )
        ) {
            Icon(Icons.Filled.PersonAdd, "add contact", tint = Color.White, modifier = Modifier.size(20.dp))
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun SearchHeader(searchValue: String, onValueChange: (String) -> Unit, onTrailingIconClick: (Boolean) -> Unit) {
    val (focused, setFocus) = remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    Box(Modifier.height(HEADER_SIZE + 1.dp)) {
        SearchTextField(
            Modifier.fillMaxSize().focusRequester(focusRequester),
            searchValue,
            onValueChange,
            onTrailingIconClick,
            focused,
            setFocus
        )
        AnimatedVisibility(visible = !focused, enter = fadeIn(), exit = fadeOut()) {
            SearchCard(focus = focused, setFocus = setFocus, focusRequester = focusRequester)
        }
    }
}