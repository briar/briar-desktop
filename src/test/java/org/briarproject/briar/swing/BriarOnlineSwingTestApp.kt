package org.briarproject.briar.swing

import dagger.Component
import org.briarproject.bramble.BrambleCoreEagerSingletons
import org.briarproject.bramble.BrambleCoreModule
import org.briarproject.bramble.api.crypto.CryptoComponent
import org.briarproject.briar.BriarCoreEagerSingletons
import org.briarproject.briar.BriarCoreModule
import org.briarproject.briar.api.test.TestDataCreator
import javax.inject.Singleton

@Component(
    modules = [
        BrambleCoreModule::class,
        BriarCoreModule::class,
        OnlineSwingTestModule::class
    ]
)
@Singleton
internal interface BriarOnlineSwingTestApp : BrambleCoreEagerSingletons, BriarCoreEagerSingletons {

    fun getUI(): UI

    fun getCryptoComponent(): CryptoComponent

    fun getTestDataCreator(): TestDataCreator
}
