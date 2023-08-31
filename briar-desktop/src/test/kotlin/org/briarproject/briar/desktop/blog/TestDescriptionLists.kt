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
        <h1>Definitions</h1>
        <p>Some definitions:</p>

        <dl>
          <dt>BDF</dt>
          <dd>a structured data format</dd>

          <dt>BQP</dt>
          <dd>a QR code key agreement protocol</dd>

          <dt>BHP</dt>
          <dd>a key agreement protocol</dd>

          <dt>BRP</dt>
          <dd>a discovery protocol</dd>

          <dt>BTP</dt>
          <dd>a transport layer security protocol for delay-tolerant networks</dd>

          <dt>BSP</dt>
          <dd>an application layer data synchronisation protocol for delay-tolerant networks</dd>
        </dl>
""".trimIndent()
