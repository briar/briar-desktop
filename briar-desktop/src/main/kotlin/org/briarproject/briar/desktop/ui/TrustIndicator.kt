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

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import org.briarproject.briar.api.identity.AuthorInfo
import org.briarproject.briar.desktop.theme.Lime500
import org.briarproject.briar.desktop.theme.Orange500
import org.briarproject.briar.desktop.theme.Red500
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TrustIndicator(status: AuthorInfo.Status) = Tooltip(
    text = when (status) {
        AuthorInfo.Status.NONE -> error("Unexpected status: $status")
        AuthorInfo.Status.UNKNOWN -> i18n("peer.trust.stranger")
        AuthorInfo.Status.UNVERIFIED -> i18n("peer.trust.unverified")
        AuthorInfo.Status.VERIFIED -> i18n("peer.trust.verified")
        AuthorInfo.Status.OURSELVES -> i18n("peer.trust.ourselves")
    }
) {
    if (status == AuthorInfo.Status.OURSELVES) {
        Icon(
            imageVector = Icons.Filled.Person,
            contentDescription = i18n("access.ourselves"),
            tint = Lime500,
            modifier = Modifier.size(16.dp),
        )
    } else {
        val gray = MaterialTheme.colors.onSurface
        val (first, second, third) = when (status) {
            AuthorInfo.Status.NONE -> error("Unexpected status: $status")
            AuthorInfo.Status.UNKNOWN -> Triple(Red500, gray, gray)
            AuthorInfo.Status.UNVERIFIED -> Triple(Orange500, Orange500, gray)
            AuthorInfo.Status.VERIFIED -> Triple(Lime500, Lime500, Lime500)
            AuthorInfo.Status.OURSELVES -> error("Unexpected status: $status")
        }
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = first)) { append("#") }
                withStyle(SpanStyle(color = second)) { append("#") }
                withStyle(SpanStyle(color = third)) { append("#") }
            },
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
        )
    }
}
