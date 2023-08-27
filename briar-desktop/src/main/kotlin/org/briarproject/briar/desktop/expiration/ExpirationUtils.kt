/*
 * Briar Desktop
 * Copyright (C) 2021-2023 The Briar Project
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

package org.briarproject.briar.desktop.expiration

import kotlinx.coroutines.delay
import org.briarproject.briar.desktop.BuildData
import java.time.Instant
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds

object ExpirationUtils {

    private val EXPIRE_AFTER = BuildData.GIT_TIME + 181.days.inWholeMilliseconds
    private val SHOW_THRESHOLD = 60.days.inWholeMilliseconds
    private val CHECK_INTERVAL = 1.hours.inWholeMilliseconds
    private val HIDE_INTERVAL = 1.days.inWholeMilliseconds

    // for testing uncomment the following instead
    // private val EXPIRE_AFTER = Instant.now().toEpochMilli() + 30.seconds.inWholeMilliseconds
    // private val SHOW_THRESHOLD = 10.seconds.inWholeMilliseconds
    // private val CHECK_INTERVAL = 1.seconds.inWholeMilliseconds
    // private val HIDE_INTERVAL = 10.seconds.inWholeMilliseconds

    private fun getMillisLeft() = (EXPIRE_AFTER - Instant.now().toEpochMilli()).milliseconds

    private fun getDaysLeft() = getMillisLeft().inWholeDays.toInt()

    private fun isExpired() = getMillisLeft() <= 0.milliseconds

    private fun expiryIsFar() = getMillisLeft() > SHOW_THRESHOLD.milliseconds

    private fun hideThreshold() = System.currentTimeMillis() - HIDE_INTERVAL

    suspend fun periodicallyCheckIfExpired(
        reportDaysLeft: (Int) -> Unit,
        reportHideThreshold: (Long) -> Unit,
        onExpiry: () -> Unit,
    ) {
        while (true) {
            if (expiryIsFar()) {
                // do nothing
            } else if (isExpired()) {
                onExpiry()
                break
            } else {
                reportDaysLeft(getDaysLeft())
                reportHideThreshold(hideThreshold())
            }
            delay(CHECK_INTERVAL)
        }
    }
}
