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

package org.briarproject.briar.desktop.testdata.forum

import org.briarproject.briar.desktop.testdata.contact.Contact
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random

@DslMarker
annotation class PostsDsl

/**
 * Define a list of forums.
 */
fun forums(block: ForumsBuilder.() -> Unit) = ForumsBuilder().apply(block).build()

@PostsDsl
class ForumsBuilder {

    private val forums = mutableListOf<Forum>()

    /**
     * Add a new forum to the forum list.
     */
    fun forum(block: ForumBuilder.() -> Unit) {
        forums.add(ForumBuilder().apply(block).build())
    }

    fun build() = forums
}

@PostsDsl
class ForumBuilder : PostHierarchyBuilder() {

    /**
     * The name of the forum.
     * Needs to be explicitly set.
     */
    lateinit var name: String

    private var memberList = mutableListOf<PostAuthor>(PostAuthor.Me)
    override var lastReplySent: LocalDateTime = LocalDateTime.now()
    override val members: Set<PostAuthor>
        get() = memberList.toSet()

    /**
     * Create and return a new member for this forum.
     * You can use the return value as the `author` of a [post].
     */
    fun memberContact(contact: Contact, sharedForum: Boolean = true) =
        PostAuthor.ContactAuthor(contact, sharedForum).also { memberList.add(it) }

    /**
     * Create and return a new member for this forum.
     * You can use the return value as the `author` of a [post].
     */
    fun memberStranger(name: String) =
        PostAuthor.StrangerAuthor(name).also { memberList.add(it) }

    /**
     * Reference to the local author.
     * You can use this as the `author` of a [post].
     */
    fun myself() = PostAuthor.Me

    fun build(): Forum {
        check(this::name.isInitialized) { "A forum needs a name to be valid." } // NON-NLS
        check(memberList.isEmpty() || memberList.find { it is PostAuthor.ContactAuthor && it.sharedWith } != null) {
            "A forum needs to have no other members or at least one member who is a contact and with whom the forum is shared." // NON-NLS
        }
        return Forum(name, memberList, posts)
    }
}

@PostsDsl
class PostBuilder(override val members: Set<PostAuthor>, parentPostSent: LocalDateTime) : PostHierarchyBuilder() {

    /**
     * The author of the post.
     * You have to create authors using `memberContact` or `memberStranger` in a forum.
     * If not set, defaults to the local author.
     */
    var author: PostAuthor = PostAuthor.Me
        set(value) {
            check(value is PostAuthor.Me || value in members) { "$value is not member of this forum." } // NON-NLS
            field = value
        }

    /**
     * The text of the post.
     * Needs to be explicitly set.
     */
    lateinit var text: String

    private var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    private var _date = parentPostSent.addRandomDelay()

    /**
     * The date of the post.
     * Can be set to a [String] matching the format `yyyy-MM-dd HH:mm:ss`
     * or to a [LocalDateTime] object.
     *
     * If not set, defaults to [LocalDateTime.now] for the first post in a forum,
     * and to a random time between zero and five minutes
     * after the last post in the corresponding thread for all subsequent ones.
     */
    var date: Any = ""
        set(value) {
            if (value is String) {
                _date = LocalDateTime.parse(value, formatter)
            } else if (value is LocalDateTime) {
                _date = value
            }
            lastReplySent = _date
        }

    override var lastReplySent: LocalDateTime = _date

    fun build(): Post {
        check(this::text.isInitialized) { "A forum post needs to contain a text to be valid." } // NON-NLS
        return Post(author, text, _date, posts)
    }
}

@PostsDsl
abstract class PostHierarchyBuilder {
    protected val posts = mutableListOf<Post>()
    protected abstract var lastReplySent: LocalDateTime
    protected abstract val members: Set<PostAuthor>

    /**
     * Add a new post to the forum or a new reply to the enclosing post.
     */
    fun post(block: PostBuilder.() -> Unit) {
        val post = PostBuilder(members, lastReplySent).apply(block).build()
        posts.add(post)
        lastReplySent = post.date
    }
}

private fun LocalDateTime.addRandomDelay() = plusSeconds(Random.nextLong(5 * 60))
