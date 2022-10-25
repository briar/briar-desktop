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

package org.briarproject.briar.desktop

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.runDesktopComposeUiTest
import org.briarproject.bramble.BrambleCoreEagerSingletons
import org.briarproject.bramble.api.plugin.TorConstants.DEFAULT_CONTROL_PORT
import org.briarproject.bramble.api.plugin.TorConstants.DEFAULT_SOCKS_PORT
import org.briarproject.briar.BriarCoreEagerSingletons
import org.briarproject.briar.desktop.TestUtils.getDataDir
import org.jetbrains.skia.Image
import org.junit.Test
import java.io.FileOutputStream

@OptIn(ExperimentalTestApi::class)
class ScreenshotTest {
    @Test
    fun makeScreenshot() = runDesktopComposeUiTest(700, 500) {
        val dataDir = getDataDir()
        val app =
            DaggerBriarDesktopTestApp.builder().desktopCoreModule(
                DesktopCoreModule(dataDir, DEFAULT_SOCKS_PORT, DEFAULT_CONTROL_PORT)
            ).build()
        // We need to load the eager singletons directly after making the
        // dependency graphs
        BrambleCoreEagerSingletons.Helper.injectEagerSingletons(app)
        BriarCoreEagerSingletons.Helper.injectEagerSingletons(app)

        val ui = app.getBriarUi()

        setContent {
            ui.content()
        }
        captureToImage().save("before-click.png")
        onNodeWithTag("close_expiration").performClick()
        captureToImage().save("after-click.png")
    }
}

private fun Image.save(file: String) {
    encodeToData()?.bytes?.let { bytes ->
        FileOutputStream(file).use { out ->
            out.write(bytes)
        }
    }
}
