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

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString

fun StringBuilder.appendLeading(leading: String, text: String? = null) {
    append(leading)
    if (text != null) append(text)
}

fun StringBuilder.appendCommaSeparated(text: String? = null) =
    appendLeading(", ", text)

fun StringBuilder.appendAfterColon(text: String? = null) =
    appendLeading(": ", text)

/**
 * Builds a new string by populating newly created [StringBuilder] using provided [builder]
 * and then converting it to a blank [AnnotatedString] (without annotations).
 *
 * If a bare [String] is needed, use [buildString] instead.
 * If an [AnnotatedString] with actual annotations is needed, use [buildAnnotatedString] instead.
 */
inline fun buildBlankAnnotatedString(builder: (StringBuilder).() -> Unit): AnnotatedString =
    AnnotatedString(StringBuilder().apply(builder).toString())
