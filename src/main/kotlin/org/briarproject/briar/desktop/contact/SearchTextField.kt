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

package org.briarproject.briar.desktop.contact

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.pointer.PointerIconDefaults
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import org.briarproject.briar.desktop.theme.surfaceVariant
import org.briarproject.briar.desktop.ui.HorizontalDivider
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchTextField(searchValue: String, onValueChange: (String) -> Unit, onContactAdd: () -> Unit) {
    val textFieldFocusRequester = remember { FocusRequester() }
    val (isSearchMode, setSearchMode) = remember { mutableStateOf(false) }

    Box(Modifier.fillMaxSize()) {
        SearchInput(
            searchValue,
            onValueChange,
            onContactAdd,
            onBack = { setSearchMode(false) },
            textFieldFocusRequester,
        )
        AnimatedVisibility(
            !isSearchMode,
            // CubicBezier ease-out curve
            enter = fadeIn(TweenSpec(durationMillis = 100, easing = CubicBezierEasing(.215f, .61f, .355f, 1f))),
            exit = fadeOut(TweenSpec(durationMillis = 100, easing = CubicBezierEasing(.215f, .61f, .355f, 1f)))
        ) {
            ContactListTopAppBar(
                onContactAdd,
                onSearch = { setSearchMode(true) },
                textFieldFocusRequester,
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ContactListTopAppBar(
    onContactAdd: () -> Unit,
    onSearch: () -> Unit,
    textFieldFocusRequester: FocusRequester
) {
    Surface(
        color = MaterialTheme.colors.surfaceVariant,
        contentColor = MaterialTheme.colors.onSurface,
        elevation = 4.dp,
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        {
                            onSearch()
                            textFieldFocusRequester.requestFocus()
                        },
                    ) {
                        Icon(Icons.Filled.Search, i18n("access.contacts.search"))
                    }
                    IconButton(
                        onClick = onContactAdd,
                        modifier = Modifier.padding(end = 14.dp).then(Modifier.size(32.dp))
                            .pointerHoverIcon(PointerIconDefaults.Default)
                    ) {
                        Icon(
                            Icons.Filled.PersonAdd,
                            i18n("access.contacts.add"),
                        )
                    }
                }
            }
            HorizontalDivider(modifier = Modifier.align(Alignment.BottomCenter))
        }
    }
}

@Composable
fun SearchInput(
    searchValue: String,
    onValueChange: (String) -> Unit,
    onContactAdd: () -> Unit,
    onBack: () -> Unit,
    textFieldFocusRequester: FocusRequester,
) {
    val focusManager = LocalFocusManager.current
    TextField(
        value = searchValue,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = LocalTextStyle.current.copy(
            color = MaterialTheme.colors.onSurface
        ),
        placeholder = { Text("Search", style = MaterialTheme.typography.body1) },
        shape = RoundedCornerShape(0.dp),
        colors = TextFieldDefaults.textFieldColors(backgroundColor = MaterialTheme.colors.surfaceVariant),
        leadingIcon = {
            IconButton(
                {
                    onValueChange("")
                    onBack()
                    focusManager.clearFocus(true)
                },
                Modifier.then(Modifier.padding(start = 20.dp, end = 16.dp).size(24.dp))
            ) {
                Icon(Icons.Filled.ArrowBack, i18n("access.contacts.search"))
            }
        },
        modifier = Modifier.fillMaxSize().focusRequester(textFieldFocusRequester)
    )
}