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

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Reply
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import org.briarproject.briar.desktop.contact.ContactListTopAppBar
import org.briarproject.briar.desktop.contact.ProfileCircle
import org.briarproject.briar.desktop.conversation.ConversationInput
import org.briarproject.briar.desktop.theme.BriarTheme
import org.briarproject.briar.desktop.ui.Constants.COLUMN_WIDTH
import org.briarproject.briar.desktop.ui.Constants.HEADER_SIZE
import org.briarproject.briar.desktop.ui.HorizontalDivider
import org.briarproject.briar.desktop.utils.PreviewUtils

@OptIn(ExperimentalMaterialApi::class)
@Suppress("HardCodedStringLiteral")
fun main() = PreviewUtils.preview() {
    val (mode, setMode) = remember { mutableStateOf(0) }
    BriarTheme(isDarkTheme = true) {
        Row(Modifier.fillMaxSize()) {
            GroupList(setMode)
            if (mode == 0) {
                GroupScreen(setMode)
            } else if (mode == 1) {
                TopicScreen(setMode)
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun GroupList(setMode: (Int) -> Unit) {
    val textFieldFocusRequester = remember { FocusRequester() }
    Card {
        Column(
            modifier = Modifier.width(COLUMN_WIDTH).fillMaxHeight().focusRequester(textFieldFocusRequester)
        ) {
            Column(Modifier.height(HEADER_SIZE).width(COLUMN_WIDTH)) {
                ContactListTopAppBar({}, {}, textFieldFocusRequester)
            }
            ExpandableCard(title = "Test0", description = "topic-", setMode = setMode)
            ExpandableCard(title = "Test1", description = "topic-", setMode = setMode)
            ExpandableCard(title = "Test2", description = "topic-", setMode = setMode)
            ExpandableCard(title = "Test3", description = "topic-", setMode = setMode)
        }
    }
}

@Composable
fun GroupHeader() {
    Column {
        Row(
            Modifier.height(HEADER_SIZE - 1.dp).fillMaxWidth().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ProfileCircle(36.dp)
                Text(
                    "Test1 /",
                    style = MaterialTheme.typography.h3,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Create, "Create", modifier = Modifier.padding(end = 16.dp))
                Icon(Icons.Filled.MoreVert, "more")
            }
        }
        HorizontalDivider()
    }
}

@Composable
fun TopicHeader(setMode: (Int) -> Unit) {
    Column {
        Row(
            Modifier.height(HEADER_SIZE - 1.dp).fillMaxWidth().padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                ProfileCircle(36.dp)
                Text(
                    "Title1 / ",
                    style = MaterialTheme.typography.h3,
                    modifier = Modifier.padding(start = 8.dp).clickable { setMode(0) }
                )
                Text(
                    "topic-1",
                    style = MaterialTheme.typography.body1,
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Create, "Create", modifier = Modifier.padding(end = 16.dp))
                Icon(Icons.Filled.MoreVert, "more")
            }
        }
        HorizontalDivider()
    }
}

@Composable
fun TopicScreen(setMode: (Int) -> Unit) {
    Scaffold(
        topBar = {
            TopicHeader(setMode)
        },
        bottomBar = {
            ConversationInput("", {}, null, {}, onSend = {})
        }
    ) {
        LazyColumn() {
            items(3) {
                val coinflip = (0..1).shuffled().last()
                MessageContents(coinflip, setMode)
            }
        }
        Spacer(Modifier.height(HEADER_SIZE))
    }
}

@Composable
fun MessageContents(type: Int, setMode: (Int) -> Unit) {
    var name = "Alice A. ###"
    var msg =
        "The fundamental concepts of classical physics, space, time, mass, and derived concepts, velocity, momentum, force, angular momentum, energy ... all rest on the principle that material points have trajectories."
    if (type == 1) {
        name = "Tom Test ###"
        msg =
            "Systems, in one sense, are devices that take input and produce an output. A system can be thought to operate on the input to produce the output. The output is related to the input by a certain relationship known as the system response. The system response usually can be modeled with a mathematical relationship between the system input and the system output."

    }
    Row(Modifier.fillMaxWidth().padding(8.dp).clickable { setMode(1) }) {
        Column(Modifier.width(48.dp)) {
            ProfileCircle(36.dp)
        }
        Column(Modifier.fillMaxWidth()) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    name,
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
                Icon(Icons.Filled.Reply, "reply")
            }
            Text(msg)
        }
    }
}

@Composable
fun GroupOverviewMessage(type: Int, setMode: (Int) -> Unit, topicNum: Int) {
    Card(Modifier.padding(start = 8.dp, top = 8.dp, bottom = 8.dp).clickable { setMode(1) }) {
        Column {
            Row(Modifier.fillMaxWidth().background(Color.DarkGray)) {
                Text(
                    "Test1 / ",
                    style = MaterialTheme.typography.h5,
                    modifier = Modifier.padding(start = 8.dp, top = 8.dp, bottom = 8.dp)
                )
                Text(
                    "topic-" + topicNum,
                    style = MaterialTheme.typography.body1,
                    modifier = Modifier.padding(top = 10.dp, bottom = 8.dp)
                )
            }
            val rand = (0..2).shuffled().last()
            for (x in (0..rand)) {
                val coinflip = (0..1).shuffled().last()
                MessageContents(coinflip, setMode)
            }
        }
    }
}

@Composable
fun GroupScreen(setMode: (Int) -> Unit) {
    val scrollState = rememberScrollState()
    Column {
        GroupHeader()
        LazyColumn {
            items(5) {
                val rand = (0..1).shuffled().last()
                if (rand == 0) {
                    GroupOverviewMessage(0, setMode, it)
                } else {
                    GroupOverviewMessage(1, setMode, it)
                }
            }
        }
    }

}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ExpandableCard(
    title: String,
    description: String,
    descriptionFontSize: TextUnit = MaterialTheme.typography.subtitle1.fontSize,
    descriptionFontWeight: FontWeight = FontWeight.Normal,
    descriptionMaxLines: Int = 4,
    padding: Dp = 12.dp,
    setMode: (Int) -> Unit
) {
    var expandedState by remember { mutableStateOf(false) }
    val rotationState by animateFloatAsState(
        targetValue = if (expandedState) 0f else 90f
    )
    Card(
        modifier = Modifier
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 150,
                    easing = CubicBezierEasing(.215f, .61f, .355f, 1f)
                )
            ),
        shape = RectangleShape,
        onClick = {
            expandedState = !expandedState
            setMode(0)
        }
    ) {
        Column(
            modifier = Modifier.width(COLUMN_WIDTH)
        ) {
            Row(
                modifier = Modifier.height(HEADER_SIZE).fillMaxWidth().padding(start = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ProfileCircle(36.dp)
                    Text(
                        text = title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                IconButton(
                    modifier = Modifier
                        .rotate(rotationState),
                    onClick = {
                        expandedState = !expandedState
                    }) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Drop-Down Arrow"
                    )
                }
            }
            var padding = PaddingValues(start = 64.dp, top = 8.dp, bottom = 8.dp)
            if (expandedState) {
                Card(Modifier.fillMaxWidth().padding(1.dp).clickable { setMode(1) }) {
                    Text(
                        text = description,
                        fontSize = descriptionFontSize,
                        fontWeight = descriptionFontWeight,
                        maxLines = descriptionMaxLines,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(padding)
                    )
                }
                Card(Modifier.fillMaxWidth().padding(1.dp).clickable { setMode(1) }) {
                    Text(
                        text = description + "1",
                        fontSize = descriptionFontSize,
                        fontWeight = descriptionFontWeight,
                        maxLines = descriptionMaxLines,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(padding)
                    )
                }
                Card(Modifier.fillMaxWidth().padding(1.dp).clickable { setMode(1) }) {
                    Text(
                        text = description + "2",
                        fontSize = descriptionFontSize,
                        fontWeight = descriptionFontWeight,
                        maxLines = descriptionMaxLines,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(padding)
                    )
                }
                Card(Modifier.fillMaxWidth().padding(1.dp).clickable { setMode(1) }) {
                    Text(
                        text = description + "3",
                        fontSize = descriptionFontSize,
                        fontWeight = descriptionFontWeight,
                        maxLines = descriptionMaxLines,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(padding)
                    )
                }
                Row(Modifier.fillMaxWidth().padding(1.dp).clickable {}) {
                    Text(
                        text = "More topics",
                        fontSize = MaterialTheme.typography.overline.fontSize,
                        maxLines = descriptionMaxLines,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(start = 64.dp, top = 12.dp, bottom = 8.dp)
                    )
                }
            }
            HorizontalDivider()
        }
    }
}
