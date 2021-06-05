package org.briarproject.briar.swing

import com.github.weisj.darklaf.LafManager
import com.github.weisj.darklaf.theme.DarculaTheme

fun main() {
    LafManager.setTheme(DarculaTheme())
    LafManager.install()
    UiTests.runApp()
}
