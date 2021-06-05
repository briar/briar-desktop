package org.briarproject.briar.swing

import dagger.Component
import org.briarproject.bramble.BrambleCoreEagerSingletons
import org.briarproject.bramble.BrambleCoreModule
import org.briarproject.briar.BriarCoreEagerSingletons
import org.briarproject.briar.BriarCoreModule
import java.security.SecureRandom
import javax.inject.Singleton

@Component(
    modules = [
        BrambleCoreModule::class,
        BriarCoreModule::class,
        SwingModule::class
    ]
)
@Singleton
internal interface BriarSwingApp : BrambleCoreEagerSingletons, BriarCoreEagerSingletons {

    fun getUI(): UI

    fun getSecureRandom(): SecureRandom

}
