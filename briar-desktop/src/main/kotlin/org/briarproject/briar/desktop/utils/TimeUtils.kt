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

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.temporal.ChronoUnit

object TimeUtils {

    fun getFormattedTimestamp(timestamp: Long): String =
        localDateTimeWrapper(timestamp, ::getFormattedTimestamp)

    fun getFormattedTimestamp(messageTime: LocalDateTime, currentTime: LocalDateTime): String =
        when {
            ChronoUnit.HOURS.between(messageTime, currentTime) < 18 ->
                getFormattedRelativeTimestamp(messageTime, currentTime)
            ChronoUnit.WEEKS.between(messageTime, currentTime) < 1 ->

            else ->
                getFormattedAbsoluteTimestamp(messageTime, currentTime)
        }

    fun getFormattedRelativeTimestamp(timestamp: Long): String =
        localDateTimeWrapper(timestamp, ::getFormattedRelativeTimestamp)

    private fun getFormattedRelativeTimestamp(messageTime: LocalDateTime, currentTime: LocalDateTime): String {
        return when {
            ChronoUnit.HOURS.between(messageTime, currentTime) < 18 ->
                getFormattedRelativeTimestamp(timestamp)
            ChronoUnit.WEEKS.between(messageTime, currentTime) < 1 ->

            else ->
                getFormattedAbsoluteTimestamp(timestamp)
        }
    }

    fun getFormattedAbsoluteTimestamp(timestamp: Long): String =
        localDateTimeWrapper(timestamp, ::getFormattedAbsoluteTimestamp)

    private fun getFormattedAbsoluteTimestamp(messageTime: LocalDateTime, currentTime: LocalDateTime): String {
        val formatter = if (ChronoUnit.DAYS.between(messageTime, currentTime) < 1) {
            DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
        } else {
            DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
        }.withLocale(InternationalizationUtils.locale)
        return messageTime.format(formatter)
    }

    private fun localDateTimeWrapper(
        timestamp: Long,
        callback: (messageTime: LocalDateTime, currentTime: LocalDateTime) -> String
    ): String {
        val messageTime = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(timestamp), ZoneId.systemDefault()
        )
        val currentTime = LocalDateTime.now()
        return callback(messageTime, currentTime)
    }
}
