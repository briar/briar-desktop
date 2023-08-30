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
        <h1>Headline</h1>
        <p>some text</p>
        <h2>second headline</h2>
        <p>
            Hello World
            <b>bold</b>, <i>italic</i>, <u>underline</u>, <strike>strikethrough</strike>, <b><i><u>all three <strike>or four</strike></u></i></b>
        </p>
        <p>
            This paragraph<br/>
            contains a <a href="https://google.com">link to Google</a> and something <small>very</small> small.
        </p>
        <blockquote>
            This is a block quote
            <br/>
            with multiple lines.
        </blockquote>
        This text is without surrounding paragraph
        and contains <span style='color: red;'>foreground</span> and <span style='background: #111111'>background</span> colors.
        <br/>
        Mixed lists:
        <ul>
          <li>foo</li>
          <li>direct children<ul><li>child1</li><li>child2</li></ul></li>
          <ul>
            <li>bar1</li>
            <li>bar2</li>
          </ul>
          <ol>
            <li>bar1</li>
            <li>bar2</li>
          </ol>
          <li>foo</li>
        </ul>
""".trimIndent()
