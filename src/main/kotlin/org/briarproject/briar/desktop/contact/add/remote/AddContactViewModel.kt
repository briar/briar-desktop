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
import org.briarproject.briar.desktop.viewmodel.BriarExecutors
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
) : DbViewModel(briarExecutors, lifecycleManager, db) {

    override fun onInit() {
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

    private fun fetchHandshakeLink() = runOnDbThread {
        val link = contactManager.handshakeLink
        _handshakeLink.postValue(link)
    }

    fun onSubmitAddContactDialog() {
        val link = _remoteHandshakeLink.value
        val alias = _alias.value
        addPendingContact(link, alias)
    }

    private fun addPendingContact(link: String, alias: String) {
        if (_handshakeLink.value == link) {
            println("Please enter contact's link, not your own")
            return
        }
        if (remoteHandshakeLinkIsInvalid(link)) {
            println("Remote handshake link is invalid")
            return
        }
        if (aliasIsInvalid(alias)) {
            println("Alias is invalid")
            return
        }

        runOnDbThread {
            try {
                contactManager.addPendingContact(link, alias)
            } catch (e: FormatException) {
                println("Link is invalid")
                println(e.stackTrace)
            } catch (e: GeneralSecurityException) {
                println("Public key is invalid")
                println(e.stackTrace)
            }
            /*
            TODO: Warn user that the following two errors might be an attack

             Use `e.pendingContact.id.bytes` and `e.pendingContact.alias` to implement the following logic:
             https://code.briarproject.org/briar/briar-gtk/-/merge_requests/97

            */
            catch (e: ContactExistsException) {
                println("Contact already exists")
                println(e.stackTrace)
            } catch (e: PendingContactExistsException) {
                println("Pending Contact already exists")
                println(e.stackTrace)
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
