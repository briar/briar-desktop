package org.briarproject.briar.desktop.introduction

import androidx.compose.runtime.mutableStateOf
import org.briarproject.bramble.api.connection.ConnectionRegistry
import org.briarproject.bramble.api.contact.ContactManager
import org.briarproject.bramble.api.db.TransactionManager
import org.briarproject.bramble.api.event.EventBus
import org.briarproject.bramble.api.lifecycle.LifecycleManager
import org.briarproject.briar.api.conversation.ConversationManager
import org.briarproject.briar.desktop.contact.ContactItem
import org.briarproject.briar.desktop.contact.ContactsViewModel
import org.briarproject.briar.desktop.contact.RealContactItem
import org.briarproject.briar.desktop.threading.BriarExecutors
import org.briarproject.briar.desktop.viewmodel.asState
import javax.inject.Inject

class IntroductionViewModel
@Inject
constructor(
    contactManager: ContactManager,
    conversationManager: ConversationManager,
    connectionRegistry: ConnectionRegistry,
    briarExecutors: BriarExecutors,
    lifecycleManager: LifecycleManager,
    db: TransactionManager,
    eventBus: EventBus,
) : ContactsViewModel(
    contactManager, conversationManager, connectionRegistry, briarExecutors, lifecycleManager, db, eventBus
) {

    private val _firstContact = mutableStateOf<RealContactItem?>(null)
    private val _secondContact = mutableStateOf<RealContactItem?>(null)
    private val _secondScreen = mutableStateOf(false)
    private val _introductionMessage = mutableStateOf("")

    val firstContact = _firstContact.asState()
    val secondContact = _secondContact.asState()
    val secondScreen = _secondScreen.asState()
    val introductionMessage = _introductionMessage.asState()

    fun setFirstContact(contactItem: RealContactItem) {
        _firstContact.value = contactItem
        loadContacts()
        backToFirstScreen()
    }

    fun setSecondContact(contactItem: RealContactItem) {
        _secondContact.value = contactItem
        _secondScreen.value = true
    }

    fun backToFirstScreen() {
        _secondScreen.value = false
        _introductionMessage.value = ""
    }

    fun setIntroductionMessage(msg: String) {
        _introductionMessage.value = msg
    }

    override fun filterContactItem(contactItem: ContactItem): Boolean {
        return if (contactItem is RealContactItem) {
            _firstContact.value?.idWrapper != contactItem.idWrapper
        } else false
    }
}
