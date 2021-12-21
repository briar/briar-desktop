package org.briarproject.briar.desktop

import dagger.Module
import dagger.Provides
import org.briarproject.bramble.account.AccountModule
import org.briarproject.bramble.api.FeatureFlags
import org.briarproject.bramble.api.db.DatabaseConfig
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
import org.briarproject.bramble.event.DefaultEventExecutorModule
import org.briarproject.bramble.network.JavaNetworkModule
import org.briarproject.bramble.plugin.tor.CircumventionModule
import org.briarproject.bramble.plugin.tor.DesktopTorPluginFactory
import org.briarproject.bramble.socks.SocksModule
import org.briarproject.bramble.system.ClockModule
import org.briarproject.bramble.system.DefaultTaskSchedulerModule
import org.briarproject.bramble.system.DefaultWakefulIoExecutorModule
import org.briarproject.bramble.system.DesktopSecureRandomModule
import org.briarproject.bramble.system.JavaSystemModule
import org.briarproject.bramble.util.OsUtils.isLinux
import org.briarproject.bramble.util.OsUtils.isMac
import org.briarproject.briar.api.test.TestAvatarCreator
import org.briarproject.briar.desktop.testdata.DeterministicTestDataCreator
import org.briarproject.briar.desktop.testdata.DeterministicTestDataCreatorImpl
import org.briarproject.briar.desktop.ui.BriarUi
import org.briarproject.briar.desktop.ui.BriarUiImpl
import org.briarproject.briar.test.TestModule
import java.io.File
import java.nio.file.Path
import java.util.Collections.emptyList
import javax.inject.Singleton

@Module(
    includes = [
        AccountModule::class,
        CircumventionModule::class,
        ClockModule::class,
        DefaultBatteryManagerModule::class,
        DefaultEventExecutorModule::class,
        DefaultTaskSchedulerModule::class,
        DefaultWakefulIoExecutorModule::class,
        DesktopSecureRandomModule::class,
        JavaNetworkModule::class,
        JavaSystemModule::class,
        SocksModule::class,
        TestModule::class,
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
    internal fun providePluginConfig(tor: DesktopTorPluginFactory): PluginConfig {
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
    internal fun provideFeatureFlags() = object : FeatureFlags {
        override fun shouldEnableImageAttachments() = false
        override fun shouldEnableProfilePictures() = false
        override fun shouldEnableDisappearingMessages() = false
    }

    @Provides
    internal fun provideTestAvatarCreator() = TestAvatarCreator { null }

    @Provides
    @Singleton
    internal fun provideDeterministicTestDataCreator(testDataCreator: DeterministicTestDataCreatorImpl): DeterministicTestDataCreator {
        return testDataCreator
    }
}
