package org.briarproject.briar.desktop.utils

import org.slf4j.bridge.SLF4JBridgeHandler
import java.util.logging.Level
import java.util.logging.LogManager

object LogUtils {

    fun setupLogging(level: Level) {
        SLF4JBridgeHandler.removeHandlersForRootLogger()
        SLF4JBridgeHandler.install()

        LogManager.getLogManager().getLogger("").level = level
    }
}
