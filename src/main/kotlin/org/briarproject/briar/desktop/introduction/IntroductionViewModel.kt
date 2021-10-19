package org.briarproject.briar.desktop.introduction

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import org.briarproject.bramble.api.connection.ConnectionRegistry
import org.briarproject.bramble.api.contact.Contact
import org.briarproject.bramble.api.contact.ContactManager
import org.briarproject.bramble.api.event.EventBus
import org.briarproject.briar.api.conversation.ConversationManager
import org.briarproject.briar.desktop.contact.ContactItem
import org.briarproject.briar.desktop.contact.ContactsViewModel
import java.util.logging.Logger
import javax.inject.Inject

class IntroductionViewModel
@Inject
constructor(
    contactManager: ContactManager,
    conversationManager: ConversationManager,
    connectionRegistry: ConnectionRegistry,
    eventBus: EventBus,
) : ContactsViewModel(contactManager, conversationManager, connectionRegistry) {

    companion object {
        private val LOG = Logger.getLogger(IntroductionViewModel::class.java.name)
    }

    init {
        //todo: where/when to remove listener again?
        eventBus.addListener(this)
    }

    private val _firstContact = mutableStateOf<Contact?>(null)
    private val _secondContact = mutableStateOf<Contact?>(null)
    private val _secondScreen = mutableStateOf(false)
    private val _introductionMessage = mutableStateOf("")

    override val contactList: List<ContactItem> = _contactList
    val firstContact: State<Contact?> = _firstContact
    val secondContact: State<Contact?> = _secondContact
    val secondScreen: State<Boolean> = _secondScreen
    val introductionMessage: State<String> = _introductionMessage

    fun setFirstContact(contact: Contact) {
        _firstContact.value = contact
        loadContacts()
        backToFirstScreen()
    }

    fun setSecondContact(contact: Contact) {
        _secondContact.value = contact
        _secondScreen.value = true
    }

    fun backToFirstScreen() {
        _secondScreen.value = false
    }

    fun setIntroductionMessage(msg: String) {
        _introductionMessage.value = msg
    }

    override fun filterContact(contact: Contact): Boolean {
        return _firstContact.value!!.id != contact.id
    }
}
