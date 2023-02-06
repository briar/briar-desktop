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

package org.briarproject.briar.desktop.mailbox

import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import org.briarproject.bramble.api.mailbox.MailboxConstants.API_CLIENT_TOO_OLD
import org.briarproject.bramble.api.mailbox.MailboxConstants.CLIENT_SUPPORTS
import org.briarproject.bramble.api.mailbox.MailboxConstants.PROBLEM_MS_SINCE_LAST_SUCCESS
import org.briarproject.bramble.api.mailbox.MailboxStatus
import org.briarproject.bramble.api.mailbox.MailboxVersion
import org.briarproject.briar.desktop.theme.Lime500
import org.briarproject.briar.desktop.theme.Orange500
import org.briarproject.briar.desktop.theme.Red500
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18nF
import org.briarproject.briar.desktop.utils.PreviewUtils.DropDownValues
import org.briarproject.briar.desktop.utils.PreviewUtils.preview
import org.briarproject.briar.desktop.utils.TimeUtils.getFormattedTimestamp

@Suppress("HardCodedStringLiteral")
fun main() = preview(
    "status" to DropDownValues(
        0,
        listOf("error", "problem", "mailbox too old", "briar too old", "ok")
    )
) {
    val now = System.currentTimeMillis()
    val status = when (getStringParameter("status")) {
        "error" -> MailboxStatus(now, now - PROBLEM_MS_SINCE_LAST_SUCCESS, 6, CLIENT_SUPPORTS)
        "problem" -> MailboxStatus(now, now - 36_000, 1, CLIENT_SUPPORTS)
        "mailbox too old" -> MailboxStatus(now, now, 0, listOf(MailboxVersion(0, 0)))
        "briar too old" -> MailboxStatus(now, now, 0, listOf(MailboxVersion(42, 0)))
        else -> MailboxStatus(now, now - 18_354, 0, CLIENT_SUPPORTS)
    }
    MailboxStatusScreen(status)
}

@Composable
fun MailboxStatusScreen(status: MailboxStatus?) {
    if (status == null) return // not expected to happen (for a noticeable amount of time)
    Column(
        verticalArrangement = spacedBy(16.dp),
        horizontalAlignment = CenterHorizontally,
        modifier = Modifier.fillMaxSize().padding(16.dp),
    ) {
        if (status.hasProblem(System.currentTimeMillis())) {
            MailboxStatusView(
                icon = Icons.Filled.Error,
                iconTint = Red500,
                title = i18n("mailbox.status.error"),
                lastSuccess = status.timeOfLastSuccess,
            )
            // TODO wizard button
        } else if (status.attemptsSinceSuccess > 0) {
            MailboxStatusView(
                icon = Icons.Filled.QuestionMark,
                iconTint = Orange500,
                title = i18n("mailbox.status.problem"),
                lastSuccess = status.timeOfLastSuccess,
            )
        } else if (status.mailboxCompatibility < 0) {
            MailboxStatusView(
                icon = Icons.Filled.Error,
                iconTint = Red500,
                title = if (status.mailboxCompatibility == API_CLIENT_TOO_OLD) {
                    i18n("mailbox.status.app_too_old.title")
                } else {
                    i18n("mailbox.status.mailbox_too_old.title")
                },
                lastSuccess = status.timeOfLastSuccess,
            )
            Text(
                text = if (status.mailboxCompatibility == API_CLIENT_TOO_OLD) {
                    i18n("mailbox.status.app_too_old.message")
                } else {
                    i18n("mailbox.status.mailbox_too_old.message")
                },
            )
        } else {
            MailboxStatusView(
                icon = Icons.Filled.CheckCircle,
                iconTint = Lime500,
                title = i18n("mailbox.status.connected.title"),
                lastSuccess = status.timeOfLastSuccess,
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        OutlinedButton(
            onClick = { /* TODO */ },
        ) {
            Text(
                text = i18n("mailbox.status.unlink.button"),
            )
        }
    }
}

@Composable
private fun MailboxStatusView(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    lastSuccess: Long,
) {
    Icon(
        imageVector = icon,
        contentDescription = "", // not important, we have a title
        tint = iconTint,
    )
    Text(
        text = title,
        style = MaterialTheme.typography.h3,
    )
    val lastSuccessText = if (lastSuccess < 0) {
        i18n("never")
    } else {
        i18nF("mailbox.status.last.connection", getFormattedTimestamp(lastSuccess))
    }
    Text(
        text = lastSuccessText,
        style = MaterialTheme.typography.body1,
        modifier = Modifier.alpha(0.56f)
    )
}
