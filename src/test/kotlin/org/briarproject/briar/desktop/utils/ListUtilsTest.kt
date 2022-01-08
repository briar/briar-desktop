package org.briarproject.briar.desktop.utils

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class ListUtilsTest {

    @Test
    fun addAfterLast() {

        val list = mutableListOf(1, 3, 5)

        // add to end
        var idx = list.addAfterLast(7) { it < 7 }
        assertEquals(list.lastIndex, idx)
        assertContentEquals(listOf(1, 3, 5, 7), list, "failed to insert at end")

        // add to start
        idx = list.addAfterLast(0) { it < 0 }
        assertEquals(0, idx)
        assertContentEquals(listOf(0, 1, 3, 5, 7), list, "failed to insert at start")

        // add in-between
        idx = list.addAfterLast(4) { it < 4 }
        assertEquals(3, idx)
        assertContentEquals(listOf(0, 1, 3, 4, 5, 7), list, "failed to insert in-between")

        // add to empty list
        list.clear()
        idx = list.addAfterLast(4) { it < 4 }
        assertEquals(0, idx)
        assertContentEquals(listOf(4), list, "failed to insert in empty list")
    }
}
