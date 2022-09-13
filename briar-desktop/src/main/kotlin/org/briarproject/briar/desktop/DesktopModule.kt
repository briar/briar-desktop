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

package org.briarproject.briar.desktop

import dagger.Module
import dagger.Provides
import org.briarproject.bramble.api.FeatureFlags
import javax.inject.Singleton

@Module(
    includes = [
        DesktopCoreModule::class,
    ]
)
internal class DesktopModule {
    @Provides
    internal fun provideFeatureFlags(desktopFeatureFlags: DesktopFeatureFlags) = object : FeatureFlags {
        override fun shouldEnableImageAttachments() = true
        override fun shouldEnableProfilePictures() = true
        override fun shouldEnableDisappearingMessages() = false
        override fun shouldEnablePrivateGroupsInCore() = desktopFeatureFlags.shouldEnablePrivateGroups()
        override fun shouldEnableForumsInCore() = desktopFeatureFlags.shouldEnableForums()
        override fun shouldEnableBlogsInCore() = desktopFeatureFlags.shouldEnableBlogs()
        override fun shouldEnableMailbox() = false
    }

    @Provides
    @Singleton
    internal fun provideDesktopFeatureFlags() = object : DesktopFeatureFlags {
        override fun shouldEnablePrivateGroups() = false
        override fun shouldEnableForums() = false
        override fun shouldEnableBlogs() = false
        override fun shouldEnableTransportSettings() = false
    }
}
