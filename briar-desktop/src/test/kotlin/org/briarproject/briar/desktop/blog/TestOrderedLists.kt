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
        <h1>Ordered lists</h1>
        <ol>
          <li>foo
          <li>direct children
            <ol>
                <li>child1
                <li>child2
            </ol>
          </li>
          <ol>
            <li>bar1</li>
            <li>bar2</li>
          </ol>
          <li> more direct children
            <ol>
              <li>foo
              <li>bar
                <ol>
                  <li>child1
                  <li>child2
                </ol>
              </li>
              <li>cat
            </ol>
          </li>
          <li>baz
        </ol>
""".trimIndent()
