package org.briarproject.briar.desktop.contact.add.remote

import androidx.compose.runtime.mutableStateOf
import org.briarproject.bramble.api.FormatException
import org.briarproject.bramble.api.contact.ContactManager
import org.briarproject.bramble.api.contact.HandshakeLinkConstants
import org.briarproject.bramble.api.db.ContactExistsException
import org.briarproject.bramble.api.db.PendingContactExistsException
import org.briarproject.bramble.api.db.TransactionManager
import org.briarproject.bramble.api.identity.AuthorConstants
import org.briarproject.bramble.api.lifecycle.LifecycleManager
import org.briarproject.bramble.util.StringUtils
import org.briarproject.briar.desktop.error.ErrorManager
import org.briarproject.briar.desktop.error.ErrorMessage
import org.briarproject.briar.desktop.error.ErrorMessage.Type.ERROR
import org.briarproject.briar.desktop.error.ErrorMessage.Type.WARNING
import org.briarproject.briar.desktop.threading.BriarExecutors
import org.briarproject.briar.desktop.viewmodel.DbViewModel
import org.briarproject.briar.desktop.viewmodel.asState
import java.security.GeneralSecurityException
import javax.inject.Inject

class AddContactViewModel
@Inject
constructor(
    briarExecutors: BriarExecutors,
    lifecycleManager: LifecycleManager,
    db: TransactionManager,
    private val contactManager: ContactManager,
    private val errorManager: ErrorManager,
) : DbViewModel(briarExecutors, lifecycleManager, db) {

    override fun onInit() {
        super.onInit()
        fetchHandshakeLink()
    }

    private val _alias = mutableStateOf("")
    private val _remoteHandshakeLink = mutableStateOf("")
    private val _handshakeLink = mutableStateOf("")

    val alias = _alias.asState()
    val remoteHandshakeLink = _remoteHandshakeLink.asState()
    val handshakeLink = _handshakeLink.asState()

    fun setAddContactAlias(alias: String) {
        _alias.value = alias
    }

    fun setRemoteHandshakeLink(link: String) {
        _remoteHandshakeLink.value = link
    }

    private fun fetchHandshakeLink() = runOnDbThreadWithTransaction(true) { txn ->
        val link = contactManager.getHandshakeLink(txn)
        txn.attach { _handshakeLink.value = link }
    }

    fun onSubmitAddContactDialog() {
        val link = _remoteHandshakeLink.value
        val alias = _alias.value
        addPendingContact(link, alias)
    }

    private fun addPendingContact(link: String, alias: String) {
        if (_handshakeLink.value == link) {
            errorManager.addError(ErrorMessage(ERROR, "Please enter contact's link, not your own"))
            return
        }
        if (remoteHandshakeLinkIsInvalid(link)) {
            errorManager.addError(ErrorMessage(ERROR, "Remote handshake link is invalid"))
            return
        }
        if (aliasIsInvalid(alias)) {
            errorManager.addError(ErrorMessage(ERROR, "Alias is invalid"))
            return
        }

        runOnDbThreadWithTransaction(false) { txn ->
            try {
                contactManager.addPendingContact(txn, link, alias)
            } catch (e: FormatException) {
                errorManager.addError(ErrorMessage(ERROR, "Link is invalid: $link"))
            } catch (e: GeneralSecurityException) {
                errorManager.addError(ErrorMessage(ERROR, "Public key is invalid: $link"))
            }
            /*
            TODO: Warn user that the following two errors might be an attack

             Use `e.pendingContact.id.bytes` and `e.pendingContact.alias` to implement the following logic:
             https://code.briarproject.org/briar/briar-gtk/-/merge_requests/97

            */
            catch (e: ContactExistsException) {
                errorManager.addError(ErrorMessage(WARNING, "Contact already exists: $link"))
            } catch (e: PendingContactExistsException) {
                errorManager.addError(ErrorMessage(WARNING, "Pending contact already exists: $link"))
            }
        }
    }

    private fun remoteHandshakeLinkIsInvalid(link: String): Boolean {
        return !HandshakeLinkConstants.LINK_REGEX.matcher(link).find()
    }

    private fun aliasIsInvalid(alias: String): Boolean {
        val aliasUtf8 = StringUtils.toUtf8(alias)
        return aliasUtf8.isEmpty() || aliasUtf8.size > AuthorConstants.MAX_AUTHOR_NAME_LENGTH
    }
}
