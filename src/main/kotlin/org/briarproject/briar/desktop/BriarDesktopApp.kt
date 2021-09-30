package org.briarproject.briar.desktop

import dagger.Component
import org.briarproject.bramble.BrambleCoreEagerSingletons
import org.briarproject.bramble.BrambleCoreModule
import org.briarproject.bramble.api.account.AccountManager
import org.briarproject.briar.BriarCoreEagerSingletons
import org.briarproject.briar.BriarCoreModule
import java.security.SecureRandom
import javax.inject.Singleton

@Component(
    modules = [
        BrambleCoreModule::class,
        BriarCoreModule::class,
        DesktopModule::class
    ]
)
@Singleton
internal interface BriarDesktopApp : BrambleCoreEagerSingletons, BriarCoreEagerSingletons {

    fun getUI(): UI

    fun getSecureRandom(): SecureRandom

    fun getAccountManager(): AccountManager
}
