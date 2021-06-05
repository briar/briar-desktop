package org.briarproject.briar.swing

import com.github.weisj.darklaf.LafManager
import com.github.weisj.darklaf.theme.IntelliJTheme

fun main() {
    LafManager.setTheme(IntelliJTheme())
    LafManager.install()
    UiTests.runApp()
}
