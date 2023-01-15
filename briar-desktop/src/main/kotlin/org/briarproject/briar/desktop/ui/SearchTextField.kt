/*
 * Briar Desktop
 * Copyright (C) 2021-2022 The Briar Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.briarproject.briar.desktop.ui

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.PointerIconDefaults
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.runInterruptible
import org.briarproject.briar.desktop.theme.surfaceVariant
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchTextField(
    placeholder: String,
    icon: ImageVector,
    searchValue: String,
    addButtonDescription: String,
    onValueChange: (String) -> Unit,
    onAddButtonClicked: () -> Unit
) {
    val (isSearchMode, setSearchMode) = remember { mutableStateOf(false) }

    Box(Modifier.fillMaxSize()) {
        Crossfade(isSearchMode) {
            if (it or searchValue.isNotEmpty()) {
                SearchInput(
                    searchValue,
                    onValueChange,
                    onBack = { setSearchMode(false) },
                )
            } else {
                ContactListTopAppBar(
                    onAddButtonClicked,
                    onSearch = { setSearchMode(true) },
                    icon
                )
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ContactListTopAppBar(
    onContactAdd: () -> Unit,
    onSearch: () -> Unit,
    icon: ImageVector
) {
    Surface(
        color = MaterialTheme.colors.surfaceVariant,
        contentColor = MaterialTheme.colors.onSurface,
    ) {
        Box {
            Row(
                Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    i18n("contacts.search.title"),
                    style = MaterialTheme.typography.h4,
                    modifier = Modifier.padding(start = 64.dp)
                )
                Spacer(Modifier.weight(1f))
                IconButton(
                    {
                        onSearch()
                    }
                ) {
                    Icon(Icons.Filled.Search, i18n("access.contacts.search"))
                }
                IconButton(
                    onClick = onContactAdd,
                    modifier = Modifier.padding(end = 14.dp).then(Modifier.size(32.dp))
                ) {
                    Icon(
                        icon,
                        i18n("access.contacts.add"),
                    )
                }
            }
            HorizontalDivider(modifier = Modifier.align(Alignment.BottomCenter))
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchInput(
    searchValue: String,
    onValueChange: (String) -> Unit,
    onBack: () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    fun endSearch() {
        onValueChange("")
        focusManager.clearFocus(true)
        onBack()
    }
    TextField(
        value = searchValue,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = LocalTextStyle.current.copy(
            color = MaterialTheme.colors.onSurface
        ),
        placeholder = { Text(i18n("contacts.search.placeholder"), style = MaterialTheme.typography.body1) },
        shape = RoundedCornerShape(0.dp),
        colors = TextFieldDefaults.textFieldColors(backgroundColor = MaterialTheme.colors.surfaceVariant),
        leadingIcon = {
            IconButton(
                { endSearch() },
                Modifier.padding(start = 20.dp, end = 16.dp).size(24.dp).pointerHoverIcon(PointerIconDefaults.Default)
            ) {
                Icon(Icons.Filled.ArrowBack, i18n("access.contacts.search"))
            }
        },
        trailingIcon = {
            if (searchValue.isNotEmpty()) {
                IconButton(
                    {
                        onValueChange("")
                        focusRequester.requestFocus()
                    },
                    Modifier.size(24.dp).pointerHoverIcon(PointerIconDefaults.Default)
                ) {
                    Icon(Icons.Filled.Close, "clear search text")
                }
            }
        },
        modifier = Modifier.fillMaxSize().focusRequester(focusRequester).onKeyEvent {
            if (it.key == Key.Escape) { endSearch() }
            true
        }
    )
    LaunchedEffect(Unit) {
        this.coroutineContext.job.invokeOnCompletion {
            focusRequester.requestFocus()
        }
    }
}
