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

import com.google.common.collect.Multimap
import org.briarproject.briar.desktop.builddata.GitUtil.getLastTagWithPrefix
import org.briarproject.briar.desktop.builddata.GitUtil.mapCommitsToTags
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.submodule.SubmoduleWalk
import org.gradle.api.GradleException
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

// Run via `./gradlew -p buildSrc test`. Is included in `./gradlew briar-desktop:test`, too.
@Suppress("HardCodedStringLiteral")
class GitTagsTest {

    @Test
    fun findReasonableNumberOfTags() {
        val git = desktopGit()
        val commitToTags = mapCommitsToTags(git)

        // At the time of writing this, August 2025 @0.6.4-beta there have been exactly
        // 14 versions tagged as some kind of release. It should never be fewer than that,
        // if we do not obtain at least that many tags, something is wrong.
        assert(commitToTags.keys().size >= 14)
    }

    @Test
    fun assumeAtMostOneTagPerCommit() {
        val git = desktopGit()
        val commitToTags = mapCommitsToTags(git)

        // Current practice with briar-desktop has not been to mark versions as alpha first,
        // then promote to beta and release, as briar-core does. If we ever decided to do that,
        // we will need to adapt this test here, however until then, let's assume that we have
        // at most one tag per commit
        commitToTags.keys().forEach {
            val tags = commitToTags.get(it)
            assert(tags.size < 2)
        }
    }

    @Test
    fun findLatestReleaseBriarCore() {
        val gitBriar = coreGit()
        val coreCommitToTags = mapCommitsToTags(gitBriar)

        // Get latest core tag
        val tagPrefix = "release"
        val coreReleaseTag = getLastTagWithPrefix(gitBriar, coreCommitToTags, tagPrefix)
            ?: throw GradleException("Unable to determine last core release tag")
        val coreVersion = coreReleaseTag.name.substring("refs/tags/$tagPrefix-".length)

        // This will need to be adapted whenever we upgrade briar core
        assertEquals("1.5.14", coreVersion)
    }

    @Test
    fun findSomeTagsInBriarCore() {
        val gitBriar = coreGit()
        val coreCommitToTags = mapCommitsToTags(gitBriar)

        // a typical release commit with all three tags pointing to it
        assertTags(
            arrayOf("release-1.5.14", "beta-1.5.14", "alpha-1.5.14"),
            coreCommitToTags,
            "1a603d52da68ab2e23891929089f0ee6e4721c6b",
        )

        // another one, same
        assertTags(
            arrayOf("release-1.5.13", "beta-1.5.13", "alpha-1.5.13"),
            coreCommitToTags,
            "6e4052fa877b07d969db10533885a7eb13285f8f",
        )

        // a commit that has been marked as an alpha and beta version which has never been promoted to release.
        assertTags(
            arrayOf("beta-1.5.5", "alpha-1.5.5"),
            coreCommitToTags,
            "2844adb8fa86a058a801dff95375333b780526b1",
        )

        // an ordinary commit
        assertTags(
            arrayOf(),
            coreCommitToTags,
            "a3f1ce6d8772a840c359da0de56d9ae00f426b21",
        )
    }

    private fun desktopGit(): Git {
        val dir = File(System.getProperty("user.dir")).parentFile
        return Git.open(dir)
    }

    private fun coreGit(): Git {
        val dir = File(System.getProperty("user.dir")).parentFile
        val git = Git.open(dir)
        val repository = git.repository

        // Find and open core repository
        val repositoryCore = SubmoduleWalk.getSubmoduleRepository(repository, "briar")
        val dirBriar = repositoryCore.directory
        return Git.open(dirBriar)
    }

    private fun assertTags(expectedTags: Array<String>, coreCommitToTags: Multimap<String, Ref>, commit: String) {
        val tags = coreCommitToTags.get(commit)
        assertEquals(expectedTags.size, tags.size)
        for (tag in expectedTags) {
            assertContains(tag, tags)
        }
    }

    private fun assertContains(tag: String, refs: Collection<Ref>) {
        val fullTag = "refs/tags/$tag"
        var found = false
        for (ref in refs) {
            if (ref.name == fullTag) {
                found = true
            }
        }
        assertTrue(found, "tag '$tag' not found")
    }

}
