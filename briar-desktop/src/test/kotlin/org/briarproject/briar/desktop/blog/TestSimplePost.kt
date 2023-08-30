/*
 * Briar Desktop
 * Copyright (C) 2023 The Briar Project
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

package org.briarproject.briar.desktop.blog

import org.intellij.lang.annotations.Language
import org.jetbrains.annotations.NonNls

fun main() = testHtml(testHtml)

@NonNls
@Language("HTML")
private val testHtml = """
        <h1>Headline</h1>some post text. Because we push the headlines
        as paragraph and span style, this text appears on the next line, not on the
        same as the headline.
""".trimIndent()
