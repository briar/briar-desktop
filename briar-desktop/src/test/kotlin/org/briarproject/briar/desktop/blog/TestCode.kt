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
        <h1>Code blocks</h1>
        This example contains a code block. It starts right here:
        <pre><code>
        $ ls -lrth /
        drwxr-xr-x  14 root root 4,0K Jan  4  2023 usr
        drwxr-xr-x   2 root root 4,0K Jan  4  2023 srv
        drwxr-xr-x  11 root root 4,0K Jan  4  2023 var
        drwx------   2 root root  16K Apr  7  2023 lost+found
        </code></pre>
        And here's some more regular text.
        <p>Then there's more text, an <code>inline</code> code and another code block:</p>
        <pre>
        drwxr-xr-x   5 root root 4,0K Nov 17  2023 boot
        drwx------   8 root root 4,0K Mar 29 14:27 root
        drwxr-xr-x  10 root root 4,0K Apr 13 14:44 opt
        dr-xr-xr-x 533 root root    0 Apr 23 11:41 proc
        dr-xr-xr-x  13 root root    0 Apr 23 11:41 sys
        drwxr-xr-x   4 root root 4,0K Apr 26 15:47 home
        drwxr-xr-x  23 root root 4,9K Aug 13 10:10 dev
        </pre>
        that's it.
""".trimIndent()
