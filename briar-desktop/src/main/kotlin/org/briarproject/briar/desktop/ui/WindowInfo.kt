/*
 * Briar Desktop
 * Copyright (C) 2021-2023 The Briar Project
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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.window.FrameWindowScope

interface WindowInfo {
    val windowScope: FrameWindowScope
    val windowWidth: Int
    var focused: Boolean
}

@Composable
fun rememberWindowInfo(windowScope: FrameWindowScope): WindowInfo =
    remember { WindowInfoImpl(windowScope) }

private class WindowInfoImpl(
    override val windowScope: FrameWindowScope,
    override val windowWidth: Int = windowScope.window.width,
) : WindowInfo {
    override var focused by mutableStateOf(false)
}

val LocalWindowInfo = staticCompositionLocalOf<WindowInfo?> { null }

@Composable
private fun getWindowInfo() = checkNotNull(LocalWindowInfo.current) {
    "No WindowInfo was provided via LocalWindowFocusState" // NON-NLS
}

@Composable
fun isWindowFocused() = getWindowInfo().focused

@Composable
fun getWindowScope() = getWindowInfo().windowScope

@Composable
fun getWindowWidth() = with(LocalDensity.current) { getWindowInfo().windowWidth.toDp() }
