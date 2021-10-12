package org.briarproject.briar.desktop.utils

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.ChronoUnit

object TimeUtils {

    fun getFormattedTimestamp(timestamp: Long): String {
        val messageTime = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(timestamp), ZoneId.systemDefault()
        )
        val currentTime = LocalDateTime.now()
        val difference = ChronoUnit.MINUTES.between(messageTime, currentTime)

        val formatter = if (difference < 1440) { // = 1 day
            DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
        } else {
            DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
        }
        return messageTime.format(formatter)
    }
}
