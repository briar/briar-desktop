package org.briarproject.briar.swing

import javax.swing.UIManager

fun main() {
    UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel")
    UiTests.runApp()
}
