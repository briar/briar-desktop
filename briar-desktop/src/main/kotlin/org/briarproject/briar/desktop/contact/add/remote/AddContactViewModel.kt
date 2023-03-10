/*
 * Briar Desktop
 * Copyright (C) 2021-2023 The Briar Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.briarproject.briar.desktop.contact.add.remote

import androidx.compose.runtime.mutableStateOf
import mu.KotlinLogging
import org.briarproject.bramble.api.FormatException
import org.briarproject.bramble.api.contact.ContactManager
import org.briarproject.bramble.api.contact.HandshakeLinkConstants
import org.briarproject.bramble.api.db.ContactExistsException
import org.briarproject.bramble.api.db.PendingContactExistsException
import org.briarproject.bramble.api.db.TransactionManager
import org.briarproject.bramble.api.identity.AuthorConstants
import org.briarproject.bramble.api.lifecycle.LifecycleManager
import org.briarproject.bramble.util.StringUtils
import org.briarproject.briar.desktop.threading.BriarExecutors
import org.briarproject.briar.desktop.utils.KLoggerUtils.w
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

    companion object {
        private val LOG = KotlinLogging.logger {}
    }

    sealed interface AddContactError

    data class OwnLinkError(val link: String) : AddContactError
    data class RemoteInvalidError(val link: String) : AddContactError
    data class AliasInvalidError(val link: String, val alias: String) : AddContactError
    data class LinkInvalidError(val link: String) : AddContactError
    data class PublicKeyInvalidError(val link: String) : AddContactError

    data class ContactAlreadyExistsError(val link: String, val existingName: String, val alias: String) :
        AddContactError

    data class PendingAlreadyExistsError(val link: String, val existingAlias: String, val alias: String) :
        AddContactError

    override fun onInit() {
        super.onInit()
        fetchHandshakeLink()
    }

    private val _alias = mutableStateOf("")
    private val _remoteHandshakeLink = mutableStateOf("")
    private val _handshakeLink = mutableStateOf("")
    private val _error = mutableStateOf<AddContactError?>(null)

    val alias = _alias.asState()
    val remoteHandshakeLink = _remoteHandshakeLink.asState()
    val handshakeLink = _handshakeLink.asState()
    val error = _error.asState()

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

    fun onSubmitAddContactDialog(onSuccess: () -> Unit) {
        val link = _remoteHandshakeLink.value
        val alias = _alias.value
        addPendingContact(link, alias, onSuccess)
    }

    fun clearError() {
        _error.value = null
    }

    private fun addPendingContact(link: String, alias: String, onSuccess: () -> Unit) {
        // ignore preceding and trailing whitespace
        val matcher = HandshakeLinkConstants.LINK_REGEX.matcher(link.trim())
        // check if the link is well-formed
        if (!matcher.matches()) {
            LOG.w { "Remote handshake link is invalid" }
            _error.value = RemoteInvalidError(link)
            return
        }
        // compare with own link
        val withoutSchema = matcher.group(2)
        val withSchema = "briar://$withoutSchema" // NON-NLS
        if (_handshakeLink.value == withSchema) {
            LOG.w { "Please enter contact's link, not your own" }
            _error.value = OwnLinkError(link)
            return
        }

        if (aliasIsInvalid(alias)) {
            LOG.w { "Alias is invalid" }
            _error.value = AliasInvalidError(link, alias)
            return
        }

        runOnDbThreadWithTransaction(false) { txn ->
            try {
                contactManager.addPendingContact(txn, link, alias)
                txn.attach {
                    onSuccess()
                    _alias.value = ""
                    _remoteHandshakeLink.value = ""
                }
            } catch (e: FormatException) {
                LOG.w { "Link is invalid: $link" }
                _error.value = LinkInvalidError(link)
            } catch (e: GeneralSecurityException) {
                LOG.w { "Public key is invalid: $link" }
                _error.value = PublicKeyInvalidError(link)
            }
            /*
            TODO: Improve warnings about potential attacks implemented here.
             See https://code.briarproject.org/briar/briar-desktop/-/issues/240
            */
            catch (e: ContactExistsException) {
                LOG.w { "Contact already exists: $link" }
                _error.value = ContactAlreadyExistsError(link, e.remoteAuthor.name, alias)
            } catch (e: PendingContactExistsException) {
                LOG.w { "Pending contact already exists: $link" }
                _error.value = PendingAlreadyExistsError(link, e.pendingContact.alias, alias)
            }
        }
    }

    private fun aliasIsInvalid(alias: String): Boolean {
        val aliasUtf8 = StringUtils.toUtf8(alias)
        return aliasUtf8.isEmpty() || aliasUtf8.size > AuthorConstants.MAX_AUTHOR_NAME_LENGTH
    }
}
