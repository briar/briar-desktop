/*
 * Briar Desktop
 * Copyright (C) 2025 The Briar Project
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

package org.briarproject.briar.desktop.builddata

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.revwalk.RevTag
import org.eclipse.jgit.revwalk.RevWalk

internal object GitUtil {

    // It is quite common at least in briar core that alpha/beta/release tags point to
    // the same commit. Hence, there is no one-to-one relationship between commits and
    // tags, in reality a commit can map to multiple tags. That is why we use a multimap
    // here.
    fun mapCommitsToTags(git: Git): Multimap<String, Ref> {
        // Build list of tags ordered by creation date. We do this to make sure that when we map
        // commits to tags below, a commit that has multiple tags pointing to it maps to the latest tag
        // (like a beta tag that is later promoted to a release tag).
        val walk = RevWalk(git.repository)

        val tagsByCreationDate = git.tagList().call().filter { tag ->
            // Skip all tags that are not annotated ones
            walk.parseAny(tag.objectId) is RevTag
        }.sortedBy { tag ->
            val revTag = walk.parseTag(tag.objectId)
            revTag.taggerIdent.`when`
        }

        // Build map of commits to tags that point to them
        val commitToTag = HashMultimap.create<String, Ref>()
        tagsByCreationDate.forEach { tag ->
            val peeled = git.repository.refDatabase.peel(tag)
            val call = git.log().add(peeled.peeledObjectId).call()
            val commit = call.iterator().next()
            commitToTag.put(commit.name, tag)
        }

        return commitToTag
    }

    fun getLastCommit(git: Git): RevCommit {
        val commits = git.log().call()
        val iterator: Iterator<RevCommit> = commits.iterator()
        if (!iterator.hasNext()) {
            throw NoSuchElementException()
        }
        return iterator.next()
    }

    fun getLastTagWithPrefix(git: Git, commitToTag: Multimap<String, Ref>, prefix: String): Ref? {
        // We used to use just the most recent tag, however that might return
        // a more recent version than we're actually using. Hence, traverse the history and
        // use the first tag found in the history starting from the current HEAD.
        val commits = git.log().call()
        val iterator: Iterator<RevCommit> = commits.iterator()
        while (iterator.hasNext()) {
            val commit = iterator.next()
            val tags = commitToTag[commit.name]
            tags.forEach { tag ->
                if (tag.name.startsWith("refs/tags/$prefix-")) {
                    return tag
                }
            }
        }
        return null
    }

}