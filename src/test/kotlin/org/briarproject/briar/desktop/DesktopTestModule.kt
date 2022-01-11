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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.swing.Swing
import org.briarproject.bramble.account.AccountModule
import org.briarproject.bramble.api.FeatureFlags
import org.briarproject.bramble.api.db.DatabaseConfig
import org.briarproject.bramble.api.event.EventExecutor
import org.briarproject.bramble.api.plugin.PluginConfig
import org.briarproject.bramble.api.plugin.TorConstants.DEFAULT_CONTROL_PORT
import org.briarproject.bramble.api.plugin.TorConstants.DEFAULT_SOCKS_PORT
import org.briarproject.bramble.api.plugin.TorControlPort
import org.briarproject.bramble.api.plugin.TorDirectory
import org.briarproject.bramble.api.plugin.TorSocksPort
import org.briarproject.bramble.api.plugin.TransportId
import org.briarproject.bramble.api.plugin.duplex.DuplexPluginFactory
import org.briarproject.bramble.api.plugin.simplex.SimplexPluginFactory
import org.briarproject.bramble.battery.DefaultBatteryManagerModule
import org.briarproject.bramble.network.JavaNetworkModule
import org.briarproject.bramble.plugin.tor.CircumventionModule
import org.briarproject.bramble.plugin.tor.UnixTorPluginFactory
import org.briarproject.bramble.socks.SocksModule
import org.briarproject.bramble.system.ClockModule
import org.briarproject.bramble.system.DefaultTaskSchedulerModule
import org.briarproject.bramble.system.DefaultWakefulIoExecutorModule
import org.briarproject.bramble.system.DesktopSecureRandomModule
import org.briarproject.bramble.system.JavaSystemModule
import org.briarproject.bramble.util.OsUtils.isLinux
import org.briarproject.bramble.util.OsUtils.isMac
import org.briarproject.briar.api.test.TestAvatarCreator
import org.briarproject.briar.attachment.AttachmentModule
import org.briarproject.briar.desktop.attachment.media.ImageCompressor
import org.briarproject.briar.desktop.attachment.media.ImageCompressorImpl
import org.briarproject.briar.desktop.testdata.DeterministicTestDataCreator
import org.briarproject.briar.desktop.testdata.DeterministicTestDataCreatorImpl
import org.briarproject.briar.desktop.testdata.TestAvatarCreatorImpl
import org.briarproject.briar.desktop.threading.BriarExecutors
import org.briarproject.briar.desktop.threading.BriarExecutorsImpl
import org.briarproject.briar.desktop.threading.UiExecutor
import org.briarproject.briar.desktop.ui.BriarUi
import org.briarproject.briar.desktop.ui.BriarUiImpl
import org.briarproject.briar.desktop.viewmodel.ViewModelModule
import org.briarproject.briar.identity.IdentityModule
import org.briarproject.briar.test.TestModule
import java.io.File
import java.nio.file.Path
import java.util.Collections.emptyList
import java.util.concurrent.Executor
import javax.inject.Singleton

@Module(
    includes = [
        AccountModule::class,
        IdentityModule::class,
        CircumventionModule::class,
        ClockModule::class,
        DefaultBatteryManagerModule::class,
        DefaultTaskSchedulerModule::class,
        DefaultWakefulIoExecutorModule::class,
        DesktopSecureRandomModule::class,
        JavaNetworkModule::class,
        JavaSystemModule::class,
        SocksModule::class,
        TestModule::class,
        ViewModelModule::class,
        AttachmentModule::class,
    ]
)
internal class DesktopTestModule(
    private val appDir: Path,
    private val socksPort: Int = DEFAULT_SOCKS_PORT,
    private val controlPort: Int = DEFAULT_CONTROL_PORT
) {

    @Provides
    @Singleton
    internal fun provideBriarService(briarService: BriarUiImpl): BriarUi = briarService

    @Provides
    @Singleton
    internal fun provideDatabaseConfig(): DatabaseConfig {
        val dbDir = appDir.resolve("db")
        val keyDir = appDir.resolve("key")
        return DesktopDatabaseConfig(dbDir, keyDir)
    }

    @Provides
    @Singleton
    @EventExecutor
    fun provideEventExecutor(): Executor = provideUiExecutor()

    @Provides
    @Singleton
    @UiExecutor
    fun provideUiExecutor(): Executor = Dispatchers.Swing.asExecutor()

    @Provides
    @Singleton
    fun provideBriarExecutors(briarExecutors: BriarExecutorsImpl): BriarExecutors = briarExecutors

    @Provides
    @TorDirectory
    internal fun provideTorDirectory(): File {
        return appDir.resolve("tor").toFile()
    }

    @Provides
    @Singleton
    @TorSocksPort
    internal fun provideTorSocksPort() = socksPort

    @Provides
    @Singleton
    @TorControlPort
    internal fun provideTorControlPort() = controlPort

    @Provides
    internal fun providePluginConfig(tor: UnixTorPluginFactory): PluginConfig {
        val duplex: List<DuplexPluginFactory> =
            if (isLinux() || isMac()) listOf(tor) else emptyList()
        return object : PluginConfig {
            override fun getDuplexFactories(): Collection<DuplexPluginFactory> = duplex
            override fun getSimplexFactories(): Collection<SimplexPluginFactory> = emptyList()
            override fun shouldPoll(): Boolean = true
            override fun getTransportPreferences(): Map<TransportId, List<TransportId>> = emptyMap()
        }
    }

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
        override fun shouldEnablePrivateGroups() = false
        override fun shouldEnableForums() = false
        override fun shouldEnableBlogs() = false
        override fun shouldEnableTransportSettings() = false
    }

    @Provides
    @Singleton
    internal fun provideImageCompressor(imageCompressor: ImageCompressorImpl): ImageCompressor {
        return imageCompressor
    }

    @Provides
    @Singleton
    internal fun provideTestAvatarCreator(testAvatarCreator: TestAvatarCreatorImpl): TestAvatarCreator {
        return testAvatarCreator
    }

    @Provides
    @Singleton
    internal fun provideDeterministicTestDataCreator(testDataCreator: DeterministicTestDataCreatorImpl): DeterministicTestDataCreator {
        return testDataCreator
    }
}
