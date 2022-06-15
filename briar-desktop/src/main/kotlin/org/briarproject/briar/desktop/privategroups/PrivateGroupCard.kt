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

package org.briarproject.briar.desktop.privategroups

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.briarproject.briar.desktop.contact.ProfileCircle
import org.briarproject.briar.desktop.theme.outline
import org.briarproject.briar.desktop.theme.selectedCard
import org.briarproject.briar.desktop.theme.surfaceVariant
import org.briarproject.briar.desktop.ui.Constants.HEADER_SIZE
import org.briarproject.briar.desktop.ui.HorizontalDivider
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18nF
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18nP
import org.briarproject.briar.desktop.utils.TimeUtils.getFormattedTimestamp

@Composable
fun PrivateGroupCard(
    privateGroupItem: PrivateGroupItem,
    onSel: () -> Unit,
    selected: Boolean,
) {
    val bgColor = if (selected) MaterialTheme.colors.selectedCard else MaterialTheme.colors.surfaceVariant
    val outlineColor = MaterialTheme.colors.outline
    val briarSecondary = MaterialTheme.colors.secondary

    Card(
        modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = HEADER_SIZE).clickable(onClick = onSel),
        shape = RoundedCornerShape(0.dp),
        backgroundColor = bgColor,
        contentColor = MaterialTheme.colors.onSurface
    ) {
        Row(horizontalArrangement = Arrangement.SpaceBetween) {
            Row(modifier = Modifier.align(Alignment.CenterVertically).padding(horizontal = 16.dp)) {
                Box(modifier = Modifier.align(Alignment.CenterVertically)) {
                    // TODO Do like `TextAvatarView` in Android
                    ProfileCircle(36.dp, privateGroupItem.privateGroup.id.bytes)
                    // Draw new message counter
                    if (privateGroupItem.unread > 0) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(6.dp, (-6).dp)
                                .height(20.dp)
                                .widthIn(min = 20.dp, max = Dp.Infinity)
                                .border(2.dp, outlineColor, CircleShape)
                                .background(briarSecondary, CircleShape)
                                .padding(horizontal = 6.dp)
                        ) {
                            Text(
                                modifier = Modifier.align(Alignment.Center),
                                fontSize = 8.sp,
                                textAlign = TextAlign.Center,
                                text = privateGroupItem.unread.toString(),
                                maxLines = 1
                            )
                        }
                    }
                }
                Column(modifier = Modifier.align(Alignment.CenterVertically).padding(start = 16.dp)) {
                    Text(
                        privateGroupItem.privateGroup.name,
                        fontSize = 14.sp,
                        modifier = Modifier.align(Alignment.Start).padding(bottom = 4.dp)
                    )
                    Text(
                        i18nF("groups.card.created", privateGroupItem.privateGroup.creator.name),
                        fontSize = 10.sp,
                        modifier = Modifier.align(Alignment.Start).padding(bottom = 2.dp)
                    )
                    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                        Text(
                            i18nP("groups.card.messages", privateGroupItem.msgCount),
                            fontSize = 10.sp,
                        )
                        Text(
                            getFormattedTimestamp(privateGroupItem.timestamp),
                            fontSize = 10.sp,
                        )
                    }
                }
            }
        }
    }
    HorizontalDivider()
}
