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

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.material.DrawerDefaults
import androidx.compose.material.DrawerValue
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalDrawer
import androidx.compose.material.Surface
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import org.briarproject.briar.desktop.ui.Constants.COLUMN_WIDTH

/**
 * State of the [InfoDrawer] composable.
 *
 * @param initialValue The initial value of the state.
 */
@Stable
class InfoDrawerState(
    initialValue: DrawerValue,
) {
    /**
     * Whether the drawer is open.
     */
    val isOpen: Boolean
        get() = currentValue == DrawerValue.Open

    /**
     * Whether the drawer is closed.
     */
    val isClosed: Boolean
        get() = currentValue == DrawerValue.Closed

    /**
     * The current value of the state.
     */
    var currentValue: DrawerValue by mutableStateOf(initialValue)
        private set

    /**
     * Open the drawer.
     */
    fun open() {
        currentValue = DrawerValue.Open
    }

    /**
     * Close the drawer.
     */
    fun close() {
        currentValue = DrawerValue.Closed
    }
}

/**
 * Create and [remember] an [InfoDrawerState].
 *
 * @param initialValue The initial value of the state.
 */
@Composable
fun rememberInfoDrawerState(initialValue: DrawerValue): InfoDrawerState {
    return remember {
        InfoDrawerState(initialValue)
    }
}

/**
 * Material Design modal info drawer.
 *
 * Modal drawers block interaction with the rest of an app’s content with a scrim.
 * They are elevated above most of the app’s UI and don’t affect the screen’s layout grid.
 *
 * This Composable is heavily inspired by [ModalDrawer], but simplified and adapted to our use-case
 * of a modal drawer opening from the end of the screen.
 *
 * @param drawerContent composable that represents content inside the drawer
 * @param modifier optional modifier for the drawer
 * @param drawerState state of the drawer
 * @param drawerShape shape of the drawer sheet
 * @param drawerElevation drawer sheet elevation. This controls the size of the shadow below the
 * drawer sheet
 * @param drawerBackgroundColor background color to be used for the drawer sheet
 * @param drawerContentColor color of the content to use inside the drawer sheet. Defaults to
 * either the matching content color for [drawerBackgroundColor], or, if it is not a color from
 * the theme, this will keep the same value set above this Surface.
 * @param scrimColor color of the scrim that obscures content when the drawer is open
 * @param content content of the rest of the UI
 *
 * @throws IllegalStateException when parent has [Float.POSITIVE_INFINITY] width
 */
@Composable
fun InfoDrawer(
    drawerContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    drawerState: InfoDrawerState = rememberInfoDrawerState(DrawerValue.Closed),
    drawerShape: Shape = MaterialTheme.shapes.large,
    drawerElevation: Dp = DrawerDefaults.Elevation,
    drawerBackgroundColor: Color = MaterialTheme.colors.surface,
    drawerContentColor: Color = contentColorFor(drawerBackgroundColor),
    scrimColor: Color = DrawerDefaults.scrimColor,
    content: @Composable () -> Unit,
) {
    val animatedOffset by animateDpAsState(if (drawerState.isClosed) COLUMN_WIDTH else 0.dp)
    BoxWithConstraints(modifier.fillMaxSize()) {
        Box { content() }
        Scrim(
            open = drawerState.isOpen,
            onClose = { drawerState.close() },
            color = scrimColor
        )
        Surface(
            modifier = Modifier
                .fillMaxHeight()
                .requiredWidth(COLUMN_WIDTH)
                .align(CenterEnd)
                .offset { IntOffset(animatedOffset.roundToPx(), 0) },
            shape = drawerShape,
            color = drawerBackgroundColor,
            contentColor = drawerContentColor,
            elevation = drawerElevation,
            content = drawerContent
        )
    }
}

@Composable
private fun Scrim(
    open: Boolean,
    onClose: () -> Unit,
    color: Color,
) {
    val alpha by animateFloatAsState(if (open) 1f else 0f)
    val dismissDrawer = if (open) {
        Modifier.pointerInput(onClose) { detectTapGestures { onClose() } }
    } else {
        Modifier
    }

    Canvas(
        Modifier
            .fillMaxSize()
            .then(dismissDrawer)
    ) {
        drawRect(color, alpha = alpha)
    }
}
