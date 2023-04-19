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
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.swing.Swing
import org.briarproject.bramble.account.AccountModule
import org.briarproject.bramble.api.crypto.CryptoExecutor
import org.briarproject.bramble.api.db.DatabaseConfig
import org.briarproject.bramble.api.event.EventExecutor
import org.briarproject.bramble.api.mailbox.MailboxDirectory
import org.briarproject.bramble.api.plugin.TorConstants.DEFAULT_CONTROL_PORT
import org.briarproject.bramble.api.plugin.TorConstants.DEFAULT_SOCKS_PORT
import org.briarproject.bramble.api.plugin.TorControlPort
import org.briarproject.bramble.api.plugin.TorDirectory
import org.briarproject.bramble.api.plugin.TorSocksPort
import org.briarproject.bramble.battery.DefaultBatteryManagerModule
import org.briarproject.bramble.io.DnsModule
import org.briarproject.bramble.mailbox.MailboxModule
import org.briarproject.bramble.mailbox.ModularMailboxModule
import org.briarproject.bramble.network.JavaNetworkModule
import org.briarproject.bramble.plugin.tor.CircumventionModule
import org.briarproject.bramble.socks.SocksModule
import org.briarproject.bramble.system.ClockModule
import org.briarproject.bramble.system.DefaultTaskSchedulerModule
import org.briarproject.bramble.system.DefaultThreadFactoryModule
import org.briarproject.bramble.system.DefaultWakefulIoExecutorModule
import org.briarproject.bramble.system.DesktopSecureRandomModule
import org.briarproject.bramble.system.JavaSystemModule
import org.briarproject.bramble.util.OsUtils.isLinux
import org.briarproject.bramble.util.OsUtils.isMac
import org.briarproject.bramble.util.OsUtils.isWindows
import org.briarproject.briar.attachment.AttachmentModule
import org.briarproject.briar.desktop.attachment.media.ImageCompressor
import org.briarproject.briar.desktop.attachment.media.ImageCompressorImpl
import org.briarproject.briar.desktop.notification.SoundNotificationProvider
import org.briarproject.briar.desktop.notification.StubNotificationProvider
import org.briarproject.briar.desktop.notification.VisualNotificationProvider
import org.briarproject.briar.desktop.notification.linux.LibnotifyNotificationProvider
import org.briarproject.briar.desktop.notification.macos.MacOsNotificationProvider
import org.briarproject.briar.desktop.notification.windows.Toast4jNotificationProvider
import org.briarproject.briar.desktop.settings.Configuration
import org.briarproject.briar.desktop.settings.ConfigurationImpl
import org.briarproject.briar.desktop.settings.DesktopSettingsModule
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
import java.util.concurrent.Executor
import javax.inject.Singleton
import javax.swing.SwingUtilities.isEventDispatchThread

// corresponding Briar Android class in
// briar/briar-android/src/main/java/org/briarproject/briar/android/AppModule.java
// and
// briar/bramble-android/src/main/java/org/briarproject/bramble/BrambleAndroidModule.java
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
        DesktopSettingsModule::class,
        JavaNetworkModule::class,
        JavaSystemModule::class,
        SocksModule::class,
        ViewModelModule::class,
        AttachmentModule::class,
        MailboxModule::class,
        ModularMailboxModule::class,
        DefaultThreadFactoryModule::class,
        DnsModule::class,
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
    @EventExecutor
    fun provideEventExecutor(): Executor = provideUiExecutor()

    @Provides
    @Singleton
    @UiExecutor
    fun provideUiExecutor(): Executor {
        val swingExecutor = Dispatchers.Swing.asExecutor()
        return Executor { command ->
            if (isEventDispatchThread()) {
                command.run()
            } else {
                swingExecutor.execute(command)
            }
        }
    }

    @Provides
    @Singleton
    @CryptoExecutor
    fun provideCryptoDispatcher(@CryptoExecutor executor: Executor): CoroutineDispatcher =
        executor.asCoroutineDispatcher()

    @Provides
    @Singleton
    fun provideBriarExecutors(briarExecutors: BriarExecutorsImpl): BriarExecutors = briarExecutors

    @Provides
    @MailboxDirectory
    internal fun provideMailboxDirectory(): File {
        return appDir.resolve("mailbox").toFile()
    }

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
    @Singleton
    fun provideConfiguration(configuration: ConfigurationImpl): Configuration = configuration

    @Provides
    @Singleton
    internal fun provideImageCompressor(imageCompressor: ImageCompressorImpl): ImageCompressor = imageCompressor

    @Provides
    @Singleton
    internal fun provideVisualNotificationProvider(): VisualNotificationProvider =
        when {
            isLinux() -> LibnotifyNotificationProvider
            isWindows() -> Toast4jNotificationProvider
            isMac() -> MacOsNotificationProvider
            else -> StubNotificationProvider
        }

    @Provides
    @Singleton
    internal fun provideSoundNotificationProvider() = SoundNotificationProvider

    @Provides
    @Singleton
    internal fun provideMessageCounter(messageCounter: MessageCounterImpl): MessageCounter = messageCounter
}
