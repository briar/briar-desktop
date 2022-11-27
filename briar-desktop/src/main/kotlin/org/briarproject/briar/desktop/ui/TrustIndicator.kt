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
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import org.briarproject.briar.api.identity.AuthorInfo.Status
import org.briarproject.briar.api.identity.AuthorInfo.Status.NONE
import org.briarproject.briar.api.identity.AuthorInfo.Status.OURSELVES
import org.briarproject.briar.api.identity.AuthorInfo.Status.UNKNOWN
import org.briarproject.briar.api.identity.AuthorInfo.Status.UNVERIFIED
import org.briarproject.briar.api.identity.AuthorInfo.Status.VERIFIED
import org.briarproject.briar.desktop.theme.Lime500
import org.briarproject.briar.desktop.theme.Orange500
import org.briarproject.briar.desktop.theme.Red500
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TrustIndicatorShort(status: Status) = Tooltip(
    text = getDescription(status)
) {
    TrustIndicatorContent(status)
}

@Composable
fun TrustIndicatorLong(status: Status) = Row(
    horizontalArrangement = spacedBy(2.dp),
    verticalAlignment = Alignment.CenterVertically,
) {
    TrustIndicatorContent(status)
    Text(getDescription(status))
}

private fun getDescription(status: Status) = when (status) {
    NONE -> error("Unexpected status: $status")
    UNKNOWN -> i18n("peer.trust.stranger")
    UNVERIFIED -> i18n("peer.trust.unverified")
    VERIFIED -> i18n("peer.trust.verified")
    OURSELVES -> i18n("peer.trust.ourselves")
}

@Composable
private fun TrustIndicatorContent(status: Status) {
    if (status == OURSELVES) {
        Icon(
            imageVector = Icons.Filled.Person,
            contentDescription = i18n("access.ourselves"),
            tint = Lime500,
            modifier = Modifier.size(16.dp),
        )
    } else {
        val gray = MaterialTheme.colors.onSurface
        val (first, second, third) = when (status) {
            NONE -> error("Unexpected status: $status")
            UNKNOWN -> Triple(Red500, gray, gray)
            UNVERIFIED -> Triple(Orange500, Orange500, gray)
            VERIFIED -> Triple(Lime500, Lime500, Lime500)
            OURSELVES -> error("Unexpected status: $status")
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
