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

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test

@Suppress("HardCodedStringLiteral")
class ListUtilsTest {

    @Test
    fun addAfterLast() {

        val list = mutableListOf(1, 3, 5)

        // add to end
        var idx = list.addAfterLast(7) { it < 7 }
        assertEquals(list.lastIndex, idx)
        assertArrayEquals("failed to insert at end", arrayOf(1, 3, 5, 7), list.toTypedArray())

        // add to start
        idx = list.addAfterLast(0) { it < 0 }
        assertEquals(0, idx)
        assertArrayEquals("failed to insert at start", arrayOf(0, 1, 3, 5, 7), list.toTypedArray())

        // add in-between
        idx = list.addAfterLast(4) { it < 4 }
        assertEquals(3, idx)
        assertArrayEquals("failed to insert in-between", arrayOf(0, 1, 3, 4, 5, 7), list.toTypedArray())

        // add to empty list
        list.clear()
        idx = list.addAfterLast(4) { it < 4 }
        assertEquals(0, idx)
        assertArrayEquals("failed to insert in empty list", arrayOf(4), list.toTypedArray())
    }
}
