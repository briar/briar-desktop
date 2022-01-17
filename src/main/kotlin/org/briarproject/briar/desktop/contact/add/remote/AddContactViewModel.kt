/*
 * Briar Desktop
 * Copyright (C) 2021-2022 The Briar Project
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

    data class ErrorContactAlreadyExists(val link: String, val existingName: String, val alias: String) :
        AddContactError

    data class ErrorPendingAlreadyExists(val link: String, val existingAlias: String, val alias: String) :
        AddContactError

    override fun onInit() {
        super.onInit()
        fetchHandshakeLink()
    }

    private val _visible = mutableStateOf(false)
    private val _alias = mutableStateOf("")
    private val _remoteHandshakeLink = mutableStateOf("")
    private val _handshakeLink = mutableStateOf("")
    private val _error = mutableStateOf<AddContactError?>(null)

    val visible = _visible.asState()
    val alias = _alias.asState()
    val remoteHandshakeLink = _remoteHandshakeLink.asState()
    val handshakeLink = _handshakeLink.asState()
    val error = _error.asState()

    fun showDialog() {
        _visible.value = true
    }

    fun dismissDialog() {
        _visible.value = false
    }

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

    fun clearError() {
        _error.value = null
    }

    private fun addPendingContact(link: String, alias: String) {
        // ignore preceding and trailing whitespace
        val matcher = HandshakeLinkConstants.LINK_REGEX.matcher(link.trim())
        // check if the link is well-formed
        if (!matcher.matches()) {
            LOG.warn { "Remote handshake link is invalid" }
            _error.value = RemoteInvalidError(link)
            return
        }
        // compare with own link
        val withoutSchema = matcher.group(2)
        val withSchema = "briar://$withoutSchema"
        if (_handshakeLink.value == withSchema) {
            LOG.warn { "Please enter contact's link, not your own" }
            _error.value = OwnLinkError(link)
            return
        }

        if (aliasIsInvalid(alias)) {
            LOG.warn { "Alias is invalid" }
            _error.value = AliasInvalidError(link, alias)
            return
        }

        runOnDbThreadWithTransaction(false) { txn ->
            try {
                contactManager.addPendingContact(txn, link, alias)
                txn.attach {
                    _visible.value = false
                    _alias.value = ""
                    _remoteHandshakeLink.value = ""
                }
            } catch (e: FormatException) {
                LOG.warn { "Link is invalid: $link" }
                _error.value = LinkInvalidError(link)
            } catch (e: GeneralSecurityException) {
                LOG.warn { "Public key is invalid: $link" }
                _error.value = PublicKeyInvalidError(link)
            }
            /*
            TODO: Warn user that the following two errors might be an attack

             Use `e.pendingContact.id.bytes` and `e.pendingContact.alias` to implement the following logic:
             https://code.briarproject.org/briar/briar-gtk/-/merge_requests/97

            */
            catch (e: ContactExistsException) {
                LOG.warn { "Contact already exists: $link" }
                _error.value = ErrorContactAlreadyExists(link, e.remoteAuthor.name, alias)
            } catch (e: PendingContactExistsException) {
                LOG.warn { "Pending contact already exists: $link" }
                _error.value = ErrorPendingAlreadyExists(link, e.pendingContact.alias, alias)
            }
        }
    }

    private fun aliasIsInvalid(alias: String): Boolean {
        val aliasUtf8 = StringUtils.toUtf8(alias)
        return aliasUtf8.isEmpty() || aliasUtf8.size > AuthorConstants.MAX_AUTHOR_NAME_LENGTH
    }
}
