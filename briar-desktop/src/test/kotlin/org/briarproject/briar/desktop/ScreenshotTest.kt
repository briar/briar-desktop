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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.InternalTestApi
import androidx.compose.ui.test.junit4.DesktopComposeTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
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
        rule.setContent {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(16.dp),
            ) {
                var greetingVisible by remember { mutableStateOf(false) }
                if (greetingVisible) {
                    Text("Hello!")
                }
                Button(onClick = { greetingVisible = true }) {
                    Text("Show greeting")
                }
            }
        }
        rule.takeScreenshot("before-click.png")
        rule
            .onNodeWithText("Show greeting")
            .performClick()
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
