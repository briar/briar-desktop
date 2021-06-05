package org.briarproject.briar.swing.dialogs

import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import kotlin.system.exitProcess

fun main() {
    val dialog = AboutDialog(null);
    dialog.setSize(600, 500)
    dialog.isVisible = true
    dialog.addWindowListener(object : WindowAdapter() {
        override fun windowClosing(e: WindowEvent) {
            exitProcess(0)
        }
    })
}