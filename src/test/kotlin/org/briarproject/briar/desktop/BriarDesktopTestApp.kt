package org.briarproject.briar.desktop

import dagger.Component
import org.briarproject.bramble.BrambleCoreEagerSingletons
import org.briarproject.bramble.BrambleCoreModule
import org.briarproject.bramble.api.account.AccountManager
import org.briarproject.bramble.api.lifecycle.LifecycleManager
import org.briarproject.bramble.api.lifecycle.ShutdownManager
import org.briarproject.briar.BriarCoreEagerSingletons
import org.briarproject.briar.BriarCoreModule
import org.briarproject.briar.api.test.TestDataCreator
import org.briarproject.briar.desktop.testdata.DeterministicTestDataCreator
import org.briarproject.briar.desktop.ui.BriarUi
import java.security.SecureRandom
import javax.inject.Singleton

@Component(
    modules = [
        BrambleCoreModule::class,
        BriarCoreModule::class,
        DesktopTestModule::class
    ]
)
@Singleton
internal interface BriarDesktopTestApp : BrambleCoreEagerSingletons, BriarCoreEagerSingletons {

    fun getBriarUi(): BriarUi

    fun getSecureRandom(): SecureRandom

    fun getLifecycleManager(): LifecycleManager

    fun getShutdownManager(): ShutdownManager

    fun getAccountManager(): AccountManager

    fun getTestDataCreator(): TestDataCreator

    fun getDeterministicTestDataCreator(): DeterministicTestDataCreator
}
