package org.briarproject.briar.desktop.expiration

import org.briarproject.briar.desktop.BuildData
import java.time.Instant
import java.time.temporal.ChronoUnit

object ExpirationUtils {

    fun getDaysLeft() = 90 - ChronoUnit.DAYS.between(Instant.ofEpochMilli(BuildData.GIT_TIME), Instant.now()).toInt()

    fun isExpired() = getDaysLeft() <= 0
}
