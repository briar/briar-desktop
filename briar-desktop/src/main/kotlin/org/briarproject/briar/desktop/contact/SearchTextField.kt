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

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.briarproject.briar.desktop.ui.ColoredIconButton
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n

@Composable
fun SearchTextField(searchValue: String, onValueChange: (String) -> Unit, onContactAdd: () -> Unit) {
    TextField(
        value = searchValue,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = LocalTextStyle.current.copy(
            color = MaterialTheme.colors.onSurface
        ),
        placeholder = { Text(i18n("contacts.search.title"), style = MaterialTheme.typography.body1) },
        shape = RoundedCornerShape(0.dp),
        leadingIcon = {
            val padding = Modifier.padding(top = 8.dp, bottom = 8.dp, start = 32.dp, end = 4.dp)
            Icon(Icons.Filled.Search, i18n("access.contacts.search"), padding)
        },
        trailingIcon = {
            ColoredIconButton(
                icon = Icons.Filled.PersonAdd,
                iconSize = 20.dp,
                contentDescription = i18n("access.contacts.add"),
                onClick = onContactAdd,
                modifier = Modifier.padding(end = 8.dp)
            )
        },
        modifier = Modifier.fillMaxSize()
    )
}
