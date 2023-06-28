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
import org.briarproject.bramble.api.plugin.PluginConfig
import org.briarproject.bramble.api.plugin.TransportId
import org.briarproject.bramble.api.plugin.duplex.DuplexPluginFactory
import org.briarproject.bramble.api.plugin.simplex.SimplexPluginFactory
import org.briarproject.bramble.plugin.file.MailboxPluginFactory
import org.briarproject.bramble.plugin.tcp.LanTcpPluginFactory
import org.briarproject.bramble.plugin.tor.MacTorPluginFactory
import org.briarproject.bramble.plugin.tor.UnixTorPluginFactory
import org.briarproject.bramble.plugin.tor.WindowsTorPluginFactory
import org.briarproject.bramble.util.OsUtils.isLinux
import org.briarproject.bramble.util.OsUtils.isMac
import org.briarproject.bramble.util.OsUtils.isWindows
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
    }

    @Provides
    @Singleton
    internal fun provideDesktopFeatureFlags() = object : DesktopFeatureFlags {
        override fun shouldEnablePrivateGroups() = true
        override fun shouldEnableForums() = true
        override fun shouldEnableBlogs() = false
        override fun shouldEnableTransportSettings() = false
    }

    @Provides
    internal fun providePluginConfig(
        unixTor: UnixTorPluginFactory,
        macTor: MacTorPluginFactory,
        winTor: WindowsTorPluginFactory,
        lan: LanTcpPluginFactory,
        mailbox: MailboxPluginFactory,
    ): PluginConfig {
        val duplex: List<DuplexPluginFactory> = when {
            isLinux() -> listOf(unixTor, lan)
            isMac() -> listOf(macTor, lan)
            isWindows() -> listOf(winTor, lan)
            else -> listOf(lan)
        }
        return object : PluginConfig {
            override fun getDuplexFactories(): Collection<DuplexPluginFactory> = duplex
            override fun getSimplexFactories(): Collection<SimplexPluginFactory> = listOf(mailbox)
            override fun shouldPoll(): Boolean = true
            override fun getTransportPreferences(): Map<TransportId, List<TransportId>> = emptyMap()
        }
    }
}
