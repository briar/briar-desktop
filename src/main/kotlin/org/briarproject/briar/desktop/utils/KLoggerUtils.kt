package org.briarproject.briar.desktop.utils

import mu.KLogger
import org.briarproject.bramble.util.LogUtils

object KLoggerUtils {

    fun KLogger.logDuration(task: String, start: Long) {
        val duration = LogUtils.now() - start
        debug { "$task took $duration ms" }
    }
}
