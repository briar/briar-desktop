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

import org.briarproject.briar.desktop.testdata.TestData

val TestData.Contacts.anna: Contact
    get() = contact { name = "Anna" }
val TestData.Contacts.bob: Contact
    get() = contact { name = "Bob" }
val TestData.Contacts.chuck: Contact
    get() = contact { name = "Chuck" }
val TestData.Contacts.polonius: Contact
    get() = contact { name = "Polonius" }
val TestData.Contacts.dan: Contact
    get() = contact { name = "Dan" }
val TestData.Contacts.george: Contact
    get() = contact { name = "Georgy Voronoy" }
