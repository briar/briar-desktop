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

import android.graphics.Bitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.InternalTestApi
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.DesktopScreenshotTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import java.io.FileOutputStream

// From https://dev.to/pchmielowski/automate-taking-screenshots-of-android-app-with-jetpack-compose-2950
@OptIn(InternalTestApi::class)
class ScreenshotTest {

    @get:Rule
    val rule = createComposeRule()

    // Found this in compose/frameworks/support/compose/ui/ui-graphics/src/desktopTest/kotlin/androidx/compose/ui/graphics/DesktopGraphicsTest.kt
    @get:Rule
    val screenshotRule = DesktopScreenshotTestRule("compose/ui/ui-desktop/graphics")

    @Test
    fun makeScreenshot() {
        rule.takeScreenshot("before-click.png")
        rule
            .onNodeWithText("Show greeting")
            .performClick()
        rule.takeScreenshot("after-click.png")
    }
}

private fun ComposeContentTestRule.takeScreenshot(file: String) {
    onRoot()
        .captureToImage()
        .asAndroidBitmap()
        .save(file)
}

private fun Bitmap.save(file: String) {
    val path = InstrumentationRegistry.getInstrumentation().targetContext.filesDir.canonicalPath
    FileOutputStream("$path/$file").use { out ->
        compress(Bitmap.CompressFormat.PNG, 100, out)
    }
}
