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

import mu.KLogger
import org.briarproject.bramble.util.LogUtils
import org.jetbrains.annotations.NonNls

object KLoggerUtils {

    @NonNls
    fun KLogger.logDuration(task: String, start: Long) {
        val duration = LogUtils.now() - start
        d { "$task took $duration ms" }
    }

    fun KLogger.t(@NonNls msg: () -> Any?) {
        trace(msg)
    }

    fun KLogger.d(@NonNls msg: () -> Any?) {
        debug(msg)
    }

    fun KLogger.i(@NonNls msg: () -> Any?) {
        info(msg)
    }

    fun KLogger.w(@NonNls msg: () -> Any?) {
        warn(msg)
    }

    fun KLogger.e(@NonNls msg: () -> Any?) {
        error(msg)
    }

    fun KLogger.t(t: Throwable?, @NonNls msg: () -> Any?) {
        trace(t, msg)
    }

    fun KLogger.d(t: Throwable?, @NonNls msg: () -> Any?) {
        debug(t, msg)
    }

    fun KLogger.i(t: Throwable?, @NonNls msg: () -> Any?) {
        info(t, msg)
    }

    fun KLogger.w(t: Throwable?, @NonNls msg: () -> Any?) {
        warn(t, msg)
    }

    fun KLogger.e(t: Throwable?, @NonNls msg: () -> Any?) {
        error(t, msg)
    }
}
