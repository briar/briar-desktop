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

package org.briarproject.briar.desktop.testdata.contact

fun contact(block: ContactBuilder.() -> Unit): Contact = ContactBuilder().apply(block).build()

class ContactBuilder {
    lateinit var name: String
    var alias: String? = null
    // todo: support avatar

    // todo: contacts are only really created if conversation is called with this contact -> change
    fun build(): Contact {
        check(this::name.isInitialized) { "A contact needs a name to be valid." } // NON-NLS
        return Contact(name, alias)
    }
}
