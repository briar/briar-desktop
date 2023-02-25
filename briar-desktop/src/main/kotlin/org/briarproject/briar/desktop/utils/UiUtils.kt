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

package org.briarproject.briar.desktop.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import org.briarproject.briar.desktop.settings.Configuration
import java.awt.Dimension
import java.awt.GraphicsConfiguration
import java.awt.GraphicsEnvironment

object UiUtils {
    fun getContactDisplayName(name: String, alias: String?) =
        if (alias == null) name else "$alias ($name)"

    // See androidx.compose.ui.window.LayoutConfiguration
    private val GlobalDensity
        get() = GraphicsEnvironment.getLocalGraphicsEnvironment()
            .defaultScreenDevice
            .defaultConfiguration
            .density

    // See androidx.compose.ui.window.LayoutConfiguration
    private val GraphicsConfiguration.density: Float
        get() = defaultTransform.scaleX.toFloat()

    /**
     * Compute an AWT Dimension for the given width and height in dp units, taking
     * into account the LocalDensity as well as the global density as detected by the
     * local graphics environment.
     *
     * On macOS hidpi devices, the global density is usually something like 2 while on Linux
     * it is usually 1 independent of the actual density. The global density is taken into
     * account by AWT itself, so we need to remove that factor from the equation, otherwise
     * it will be accounted for twice resulting in windows that are bigger than expected.
     */
    @Composable
    fun DensityDimension(width: Int, height: Int): Dimension {
        return DensityDimension(width, height, LocalDensity.current.density)
    }

    /**
     * Compute an AWT Dimension for the given width and height in dp units, taking
     * into account the UI scale factor from the user settings as well as the global
     * density as detected by the local graphics environment.
     *
     * On macOS hidpi devices, the global density is usually something like 2 while on Linux
     * it is usually 1 independent of the actual density. The global density is taken into
     * account by AWT itself, so we need to remove that factor from the equation, otherwise
     * it will be accounted for twice resulting in windows that are bigger than expected.
     */
    @Composable
    fun DensityDimension(width: Int, height: Int, configuration: Configuration): Dimension {
        return DensityDimension(width, height, configuration.uiScale ?: GlobalDensity)
    }

    @Composable
    private fun DensityDimension(width: Int, height: Int, uiScale: Float): Dimension {
        with(Density(uiScale / GlobalDensity)) {
            return Dimension(width.dp.roundToPx(), height.dp.roundToPx())
        }
    }
}
