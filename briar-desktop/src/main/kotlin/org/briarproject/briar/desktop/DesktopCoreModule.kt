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
import org.briarproject.bramble.plugin.tcp.LanTcpPluginFactory
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
import org.briarproject.briar.attachment.AttachmentModule
import org.briarproject.briar.desktop.attachment.media.ImageCompressor
import org.briarproject.briar.desktop.attachment.media.ImageCompressorImpl
import org.briarproject.briar.desktop.notification.SoundNotificationProvider
import org.briarproject.briar.desktop.notification.StubNotificationProvider
import org.briarproject.briar.desktop.notification.VisualNotificationProvider
import org.briarproject.briar.desktop.notification.linux.LibnotifyNotificationProvider
import org.briarproject.briar.desktop.settings.Configuration
import org.briarproject.briar.desktop.settings.ConfigurationImpl
import org.briarproject.briar.desktop.settings.EncryptedSettings
import org.briarproject.briar.desktop.settings.EncryptedSettingsImpl
import org.briarproject.briar.desktop.settings.EncryptedSettingsReadOnly
import org.briarproject.briar.desktop.settings.UnencryptedSettings
import org.briarproject.briar.desktop.settings.UnencryptedSettingsImpl
import org.briarproject.briar.desktop.settings.UnencryptedSettingsReadOnly
import org.briarproject.briar.desktop.threading.BriarExecutors
import org.briarproject.briar.desktop.threading.BriarExecutorsImpl
import org.briarproject.briar.desktop.threading.UiExecutor
import org.briarproject.briar.desktop.ui.BriarUi
import org.briarproject.briar.desktop.ui.BriarUiImpl
import org.briarproject.briar.desktop.ui.MessageCounter
import org.briarproject.briar.desktop.ui.MessageCounterImpl
import org.briarproject.briar.desktop.viewmodel.ViewModelModule
import org.briarproject.briar.identity.IdentityModule
import java.io.File
import java.nio.file.Path
import java.util.Collections.emptyList
import java.util.concurrent.Executor
import javax.inject.Singleton

// corresponding Briar Android class in
// briar/briar-android/src/main/java/org/briarproject/briar/android/AppModule.java
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
        ViewModelModule::class,
        AttachmentModule::class,
    ]
)
internal class DesktopCoreModule(
    private val appDir: Path,
    private val socksPort: Int = DEFAULT_SOCKS_PORT,
    private val controlPort: Int = DEFAULT_CONTROL_PORT,
) {

    @Provides
    @Singleton
    internal fun provideBriarUi(briarUi: BriarUiImpl): BriarUi = briarUi

    @Provides
    @Singleton
    internal fun provideDatabaseConfig(): DatabaseConfig {
        val dbDir = appDir.resolve("db")
        val keyDir = appDir.resolve("key")
        return DesktopDatabaseConfig(dbDir, keyDir)
    }

    @Provides
    @Singleton
    fun provideUnencryptedSettings(settings: UnencryptedSettingsImpl): UnencryptedSettings = settings

    @Provides
    @Singleton
    // provide [UnencryptedSettings] singleton itself as provided above to use same object
    fun provideUnencryptedSettingsReadOnly(settings: UnencryptedSettings): UnencryptedSettingsReadOnly = settings

    @Provides
    @Singleton
    fun provideEncryptedSettings(settings: EncryptedSettingsImpl): EncryptedSettings = settings

    @Provides
    @Singleton
    // provide [EncryptedSettings] singleton itself as provided above to use same object
    fun provideEncryptedSettingsReadOnly(settings: EncryptedSettings): EncryptedSettingsReadOnly = settings

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
    internal fun provideTorDirectory(): File =
        appDir.resolve("tor").toFile()

    @Provides
    @Singleton
    @TorSocksPort
    internal fun provideTorSocksPort() = socksPort

    @Provides
    @Singleton
    @TorControlPort
    internal fun provideTorControlPort() = controlPort

    @Provides
    internal fun providePluginConfig(tor: UnixTorPluginFactory, lan: LanTcpPluginFactory): PluginConfig {
        val duplex: List<DuplexPluginFactory> =
            if (isLinux() || isMac()) listOf(tor, lan) else listOf(lan)
        return object : PluginConfig {
            override fun getDuplexFactories(): Collection<DuplexPluginFactory> = duplex
            override fun getSimplexFactories(): Collection<SimplexPluginFactory> = emptyList()
            override fun shouldPoll(): Boolean = true
            override fun getTransportPreferences(): Map<TransportId, List<TransportId>> = emptyMap()
        }
    }

    @Provides
    @Singleton
    fun provideConfiguration(configuration: ConfigurationImpl): Configuration = configuration

    @Provides
    @Singleton
    internal fun provideImageCompressor(imageCompressor: ImageCompressorImpl): ImageCompressor = imageCompressor

    @Provides
    @Singleton
    internal fun provideVisualNotificationProvider(): VisualNotificationProvider =
        if (isLinux()) LibnotifyNotificationProvider else StubNotificationProvider

    @Provides
    @Singleton
    internal fun provideSoundNotificationProvider() = SoundNotificationProvider

    @Provides
    @Singleton
    internal fun provideMessageCounter(messageCounter: MessageCounterImpl): MessageCounter = messageCounter
}
