package org.briarproject.briar.desktop.login

import androidx.compose.runtime.mutableStateOf
import org.briarproject.bramble.api.account.AccountManager
import org.briarproject.bramble.api.crypto.DecryptionException
import org.briarproject.bramble.api.db.TransactionManager
import org.briarproject.bramble.api.event.Event
import org.briarproject.bramble.api.event.EventBus
import org.briarproject.bramble.api.lifecycle.IoExecutor
import org.briarproject.bramble.api.lifecycle.LifecycleManager
import org.briarproject.bramble.api.lifecycle.LifecycleManager.LifecycleState
import org.briarproject.bramble.api.lifecycle.event.LifecycleEvent
import org.briarproject.briar.desktop.login.LoginViewModel.LoginState.COMPACTING
import org.briarproject.briar.desktop.login.LoginViewModel.LoginState.MIGRATING
import org.briarproject.briar.desktop.login.LoginViewModel.LoginState.SIGNED_OUT
import org.briarproject.briar.desktop.login.LoginViewModel.LoginState.SIGNING_IN
import org.briarproject.briar.desktop.login.LoginViewModel.LoginState.STARTED
import org.briarproject.briar.desktop.login.LoginViewModel.LoginState.STARTING
import org.briarproject.briar.desktop.threading.BriarExecutors
import org.briarproject.briar.desktop.threading.UiExecutor
import org.briarproject.briar.desktop.viewmodel.EventListenerDbViewModel
import org.briarproject.briar.desktop.viewmodel.asState
import javax.inject.Inject

class LoginViewModel
@Inject
constructor(
    private val accountManager: AccountManager,
    private val briarExecutors: BriarExecutors,
    private val lifecycleManager: LifecycleManager,
    eventBus: EventBus,
    db: TransactionManager,
) : EventListenerDbViewModel(briarExecutors, lifecycleManager, db, eventBus) {

    enum class LoginState {
        SIGNED_OUT, SIGNING_IN, SIGNED_IN, STARTING, MIGRATING, COMPACTING, STARTED
    }

    private val _state = mutableStateOf(SIGNED_OUT)
    private val _password = mutableStateOf("")

    val state = _state.asState()
    val password = _password.asState()

    override fun onInit() {
        super.onInit()
        updateState(lifecycleManager.lifecycleState)
    }

    override fun eventOccurred(e: Event?) {
        if (e is LifecycleEvent) {
            updateState(e.lifecycleState)
        }
    }

    private fun updateState(s: LifecycleState) {
        _state.value =
            if (accountManager.hasDatabaseKey()) {
                when {
                    s.isAfter(LifecycleState.STARTING_SERVICES) -> STARTED
                    s == LifecycleState.MIGRATING_DATABASE -> MIGRATING
                    s == LifecycleState.COMPACTING_DATABASE -> COMPACTING
                    else -> STARTING
                }
            } else {
                SIGNED_OUT
            }
    }

    fun setPassword(password: String) {
        _password.value = password
    }

    fun signIn(@UiExecutor success: () -> Unit) {
        _state.value = SIGNING_IN
        briarExecutors.onIoThread {
            try {
                accountManager.signIn(password.value)
                signedIn()

                briarExecutors.onUiThread(success)
            } catch (e: DecryptionException) {
                // failure, try again
                briarExecutors.onUiThread {
                    _state.value = SIGNED_OUT
                    _password.value = ""
                }
            }
        }
    }

    @IoExecutor
    private fun signedIn() {
        val dbKey = accountManager.databaseKey ?: throw AssertionError()
        lifecycleManager.startServices(dbKey)
        lifecycleManager.waitForStartup()
    }
}
