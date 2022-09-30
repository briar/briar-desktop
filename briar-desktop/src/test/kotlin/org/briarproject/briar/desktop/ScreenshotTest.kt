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

import androidx.compose.ui.test.InternalTestApi
import androidx.compose.ui.test.junit4.DesktopComposeTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import org.briarproject.bramble.BrambleCoreEagerSingletons
import org.briarproject.bramble.api.plugin.TorConstants.DEFAULT_CONTROL_PORT
import org.briarproject.bramble.api.plugin.TorConstants.DEFAULT_SOCKS_PORT
import org.briarproject.briar.BriarCoreEagerSingletons
import org.briarproject.briar.desktop.TestUtils.getDataDir
import org.jetbrains.skia.Color
import org.jetbrains.skia.Image
import org.jetbrains.skia.Surface
import org.junit.Rule
import org.junit.Test
import java.io.FileOutputStream

// From https://dev.to/pchmielowski/automate-taking-screenshots-of-android-app-with-jetpack-compose-2950
@OptIn(InternalTestApi::class)
class ScreenshotTest {

    @get:Rule
    val rule = createComposeRule() as DesktopComposeTestRule

    @Test
    fun makeScreenshot() {
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

        rule.setContent {
            ui.test()
        }
        rule.takeScreenshot("before-click.png")
        rule.onNodeWithTag("close_expiration").performClick()
        rule.waitForIdle()
        rule.takeScreenshot("after-click.png")
    }
}

// needs to use hard-coded size of image to work as expected (to match hard-coded size inside [DesktopComposeTestRule])
// todo: pass size with constructor, e.g., enhanced [createComposeRule(width, height)], expose surface OR
//  extension function added to SemanticsNodeInteraction in context of DesktopComposeTestRule which can make use of surface.makeImageSnapshot(bounds)
private val surface = Surface.makeRasterN32Premul(1024, 768)

@OptIn(InternalTestApi::class)
private fun DesktopComposeTestRule.takeScreenshot(file: String) {
    surface.canvas.clear(Color.TRANSPARENT)
    scene.render(surface.canvas, mainClock.currentTime)
    surface.makeImageSnapshot().save(file)
}

private fun Image.save(file: String) {
    encodeToData()?.bytes?.let { bytes ->
        FileOutputStream(file).use { out ->
            out.write(bytes)
        }
    }
}
