package org.briarproject.briar.swing.dialogs

import org.briarproject.briar.swing.config.Configuration
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import javax.swing.JFrame
import javax.swing.SwingUtilities
import javax.swing.UIManager
import kotlin.system.exitProcess

fun main() {
    val configuration = Configuration()
    val dialog = SettingsDialog(null, configuration);
    dialog.setSize(600, 500)
    dialog.isVisible = true
    dialog.addWindowListener(object : WindowAdapter() {
        override fun windowClosing(e: WindowEvent) {
            exitProcess(0)
        }
    })
    dialog.ok.addActionListener {
        dialog.setValues(configuration);
        setLookAndFeel(configuration.lookAndFeel)
    }
}

private fun setLookAndFeel(lookAndFeel: String?) {
    var lookAndFeel = lookAndFeel
    if (lookAndFeel == null) {
        lookAndFeel = UIManager.getSystemLookAndFeelClassName()
    }
    UIManager.setLookAndFeel(lookAndFeel)

    for (window in JFrame.getWindows()) {
        SwingUtilities.updateComponentTreeUI(window)
    }
}