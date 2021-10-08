package org.briarproject.briar.desktop.paul.views

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope

/**
 * Copyright 2014 www.delight.im (info@delight.im)
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
internal class Identicon(private val input: ByteArray, width: Float, height: Float) {

    companion object {
        private const val ROWS = 9
        private const val COLUMNS = 9
        private const val CENTER_COLUMN_INDEX = COLUMNS / 2 + COLUMNS % 2
    }

    private val colors: Array<Array<Color>>
    private val cellWidth: Float
    private val cellHeight: Float
    private fun getByte(index: Int): Byte {
        return input[index % input.size]
    }

    init {
        require(input.isNotEmpty())

        cellWidth = width / COLUMNS
        cellHeight = height / ROWS

        colors = Array(ROWS) { Array(COLUMNS) { Color(0) } }
        for (r in 0 until ROWS) {
            for (c in 0 until COLUMNS) {
                colors[r][c] = if (isCellVisible(r, c)) foregroundColor else backgroundColor
            }
        }
    }

    private fun isCellVisible(row: Int, column: Int): Boolean {
        val index = 3 + row * CENTER_COLUMN_INDEX + getSymmetricColumnIndex(column)
        return getByte(index) >= 0
    }

    private fun getSymmetricColumnIndex(index: Int): Int {
        return if (index < CENTER_COLUMN_INDEX) index else COLUMNS - index - 1
    }

    private val foregroundColor: Color
        get() {
            val r = getByte(0) * 3 / 4 + 96
            val g = getByte(1) * 3 / 4 + 96
            val b = getByte(2) * 3 / 4 + 96
            return Color(r, g, b)
        }

    // http://www.google.com/design/spec/style/color.html#color-themes
    private val backgroundColor: Color
        get() = Color(0xFA, 0xFA, 0xFA)

    fun draw(g: DrawScope) {
        for (r in 0 until ROWS) {
            for (c in 0 until COLUMNS) {
                val x = cellWidth * c
                val y = cellHeight * r

                g.drawRect(
                    color = colors[r][c],
                    topLeft = Offset(x, y),
                    size = Size(cellWidth, cellHeight)
                )
            }
        }
    }
}
