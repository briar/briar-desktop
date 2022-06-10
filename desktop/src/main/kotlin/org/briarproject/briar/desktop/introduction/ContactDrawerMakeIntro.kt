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

package org.briarproject.briar.desktop.introduction

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.briarproject.briar.desktop.contact.ContactCard
import org.briarproject.briar.desktop.contact.ContactItem
import org.briarproject.briar.desktop.contact.ProfileCircle
import org.briarproject.briar.desktop.theme.surfaceVariant
import org.briarproject.briar.desktop.ui.Constants.HEADER_SIZE
import org.briarproject.briar.desktop.ui.HorizontalDivider
import org.briarproject.briar.desktop.utils.InternationalizationUtils
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18n
import org.briarproject.briar.desktop.utils.InternationalizationUtils.i18nF
import org.briarproject.briar.desktop.viewmodel.viewModel

@Composable
fun ContactDrawerMakeIntro(
    contactItem: ContactItem,
    closeInfoDrawer: (reload: Boolean) -> Unit,
    viewModel: IntroductionViewModel = viewModel(),
) {
    LaunchedEffect(contactItem) {
        viewModel.setFirstContact(contactItem)
    }
    Surface(color = MaterialTheme.colors.surfaceVariant, contentColor = MaterialTheme.colors.onSurface) {
        Column {
            if (!viewModel.secondScreen.value) {
                Row(Modifier.fillMaxWidth().height(HEADER_SIZE)) {
                    IconButton(
                        onClick = { closeInfoDrawer(false) },
                        Modifier.padding(start = 24.dp).size(24.dp).align(Alignment.CenterVertically)
                    ) {
                        Icon(Icons.Filled.Close, i18n("access.introduction.close"))
                    }
                    Text(
                        text = i18nF("introduction.title_first", contactItem.displayName),
                        modifier = Modifier.align(Alignment.CenterVertically).padding(start = 16.dp),
                        style = MaterialTheme.typography.body2,
                    )
                }
                HorizontalDivider()
                LazyColumn {
                    items(viewModel.contactList.value) { contactItem ->
                        if (contactItem is ContactItem)
                            ContactCard(
                                contactItem,
                                onSel = { viewModel.setSecondContact(contactItem) },
                                selected = false,
                                onRemovePending = {},
                                padding = PaddingValues(end = 16.dp),
                            )
                    }
                }
            } else {
                Row(Modifier.fillMaxWidth().height(HEADER_SIZE)) {
                    IconButton(
                        onClick = viewModel::backToFirstScreen,
                        Modifier.padding(start = 24.dp).size(24.dp).align(Alignment.CenterVertically)
                    ) {
                        Icon(Icons.Filled.ArrowBack, i18n("access.introduction.back.contact"))
                    }
                    Text(
                        text = i18n("introduction.title_second"),
                        modifier = Modifier.align(Alignment.CenterVertically).padding(start = 16.dp),
                        style = MaterialTheme.typography.body1,
                    )
                }
                Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceAround) {
                    Column(
                        Modifier.align(Alignment.Top).weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ProfileCircle(36.dp, viewModel.firstContact.value!!)
                        Text(
                            text = viewModel.firstContact.value!!.displayName,
                            modifier = Modifier.padding(top = 4.dp).align(Alignment.CenterHorizontally),
                            style = MaterialTheme.typography.body1,
                            textAlign = TextAlign.Center,
                        )
                    }
                    Icon(Icons.Filled.SwapHoriz, i18n("access.swap"), modifier = Modifier.size(48.dp))
                    Column(
                        Modifier.align(Alignment.Top).weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ProfileCircle(36.dp, viewModel.secondContact.value!!)
                        Text(
                            text = viewModel.secondContact.value!!.displayName,
                            modifier = Modifier.padding(top = 4.dp).align(Alignment.CenterHorizontally),
                            style = MaterialTheme.typography.body1,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
                Row(Modifier.padding(8.dp)) {
                    TextField(
                        value = viewModel.introductionMessage.value,
                        onValueChange = viewModel::setIntroductionMessage,
                        placeholder = {
                            Text(
                                text = i18n("introduction.message"),
                                style = MaterialTheme.typography.body1,
                            )
                        },
                    )
                }
                Row(Modifier.padding(8.dp).weight(1f, true)) {
                    Button(
                        onClick = {
                            viewModel.makeIntroduction()
                            closeInfoDrawer(true)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val text = i18n("introduction.introduce")
                        Text(text.uppercase(InternationalizationUtils.locale))
                    }
                }
            }
        }
    }
}
