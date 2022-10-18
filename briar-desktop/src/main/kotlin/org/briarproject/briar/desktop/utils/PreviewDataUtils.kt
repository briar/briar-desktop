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

package org.briarproject.briar.desktop.utils

import org.briarproject.bramble.api.UniqueId
import org.briarproject.bramble.api.crypto.CryptoConstants.MAX_SIGNATURE_PUBLIC_KEY_BYTES
import org.briarproject.bramble.api.crypto.SignaturePublicKey
import org.briarproject.bramble.api.identity.Author
import org.briarproject.bramble.api.identity.AuthorId
import org.briarproject.bramble.api.sync.MessageId
import org.briarproject.briar.api.forum.ForumPostHeader
import org.briarproject.briar.api.identity.AuthorInfo
import kotlin.random.Random

fun getRandomAuthor(): Author = Author(
    AuthorId(getRandomId()),
    0,
    getRandomString(),
    SignaturePublicKey(Random.nextBytes(MAX_SIGNATURE_PUBLIC_KEY_BYTES))
)

fun getRandomAuthorInfo(): AuthorInfo = AuthorInfo(statusList.random())

fun getRandomForumPostHeader() = ForumPostHeader(
    MessageId(getRandomId()),
    null,
    System.currentTimeMillis(),
    getRandomAuthor(),
    getRandomAuthorInfo(),
    Random.nextBoolean() && Random.nextBoolean(),
)

private val charPool = ('a'..'z') + ('A'..'Z')
private val statusList = listOf(
    AuthorInfo.Status.UNKNOWN,
    AuthorInfo.Status.UNVERIFIED,
    AuthorInfo.Status.VERIFIED,
    AuthorInfo.Status.OURSELVES,
)

fun getRandomId() = Random.nextBytes(UniqueId.LENGTH)

fun getRandomString(length: Int = Random.nextInt(1, 23)): String = (1..length)
    .map { Random.nextInt(0, charPool.size) }
    .map(charPool::get)
    .joinToString("")
