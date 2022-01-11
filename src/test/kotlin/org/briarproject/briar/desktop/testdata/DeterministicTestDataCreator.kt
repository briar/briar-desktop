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

package org.briarproject.briar.desktop.testdata

interface DeterministicTestDataCreator {
    /**
     * Create fake test data on the IoExecutor
     *
     * @param numContacts          Number of contacts to create. Must be >= 1
     * @param numPrivateMsgs       Number of private messages to create for each contact.
     * @param avatarPercent        Percentage of contacts that will use a random profile image.
     *                             Between 0 and 100.
     * @param numPrivateGroups     Number of private groups to create. Must be >= 1
     * @param numPrivateGroupPosts Number of private group messages to create in each group
     */
    fun createTestData(
        numContacts: Int,
        numPrivateMsgs: Int,
        avatarPercent: Int,
        numPrivateGroups: Int,
        numPrivateGroupPosts: Int,
    )
}
