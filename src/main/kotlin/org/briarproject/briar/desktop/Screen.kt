package org.briarproject.briar.desktop

sealed class Screen {
    object Login: Screen()
    object Main: Screen()
}
