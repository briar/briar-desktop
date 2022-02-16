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

package org.briarproject.briar.desktop.builddata

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.submodule.SubmoduleWalk
import org.gradle.api.GradleScriptException
import org.gradle.api.internal.artifacts.PreResolvedResolvableArtifact
import org.gradle.api.tasks.TaskAction
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.StandardCopyOption

@OptIn(ExperimentalStdlibApi::class)
@Suppress("HardCodedStringLiteral")
open class GenerateBuildDataSourceTask : AbstractBuildDataTask() {

    companion object {
        val LICENSES = buildMap {
            put(UnversionedArtifact("ch.qos.logback", "logback-classic"), "EPL 1.0/LGPL 2.1")
            put(UnversionedArtifact("ch.qos.logback", "logback-core"), "EPL 1.0/LGPL 2.1")
            put(UnversionedArtifact("com.fasterxml.jackson.core", "jackson-annotations"), "Apache 2.0")
            put(UnversionedArtifact("com.fasterxml.jackson.core", "jackson-core"), "Apache 2.0")
            put(UnversionedArtifact("com.fasterxml.jackson.core", "jackson-databind"), "Apache 2.0")
            put(UnversionedArtifact("com.github.ajalt.clikt", "clikt-jvm"), "Apache 2.0")
            put(UnversionedArtifact("com.google.code.findbugs", "jsr305"), "Apache 2.0")
            put(UnversionedArtifact("com.google.dagger", "dagger"), "Apache 2.0")
            put(UnversionedArtifact("com.h2database", "h2"), "EPL 1.0/MPL 2.0")
            put(UnversionedArtifact("com.ibm.icu", "icu4j"), "ICU")
            put(UnversionedArtifact("com.rometools", "rome"), "Apache 2.0")
            put(UnversionedArtifact("com.rometools", "rome-utils"), "Apache 2.0")
            put(UnversionedArtifact("com.squareup.okhttp3", "okhttp"), "Apache 2.0")
            put(UnversionedArtifact("com.squareup.okio", "okio"), "Apache 2.0")
            put(UnversionedArtifact("io.github.microutils", "kotlin-logging-jvm"), "Apache 2.0")
            put(UnversionedArtifact("javax.inject", "javax.inject"), "Apache 2.0")
            put(UnversionedArtifact("net.i2p.crypto", "eddsa"), "CC0 1.0")
            put(UnversionedArtifact("net.java.dev.jna", "jna"), "Apache 2.0/LGPL 2.1")
            put(UnversionedArtifact("net.java.dev.jna", "jna-platform"), "Apache 2.0/LGPL 2.1")
            put(UnversionedArtifact("org.bitlet", "weupnp"), "LGPL 2.1")
            put(UnversionedArtifact("org.bouncycastle", "bcprov-jdk15to18"), "BouncyCastle")
            put(UnversionedArtifact("org.briarproject", "jtorctl"), "BSD 2.0")
            put(UnversionedArtifact("org.jdom", "jdom2"), "JDOM (Apache without acknowledgment clause)")
            put(UnversionedArtifact("org.jetbrains", "annotations"), "Apache 2.0")
            put(UnversionedArtifact("org.jetbrains.compose.animation", "animation-core-desktop"), "Apache 2.0")
            put(UnversionedArtifact("org.jetbrains.compose.animation", "animation-desktop"), "Apache 2.0")
            put(UnversionedArtifact("org.jetbrains.compose.desktop", "desktop-jvm"), "Apache 2.0")
            put(UnversionedArtifact("org.jetbrains.compose.foundation", "foundation-desktop"), "Apache 2.0")
            put(UnversionedArtifact("org.jetbrains.compose.foundation", "foundation-layout-desktop"), "Apache 2.0")
            put(UnversionedArtifact("org.jetbrains.compose.material", "material-desktop"), "Apache 2.0")
            put(UnversionedArtifact("org.jetbrains.compose.material", "material-icons-core-desktop"), "Apache 2.0")
            put(UnversionedArtifact("org.jetbrains.compose.material", "material-icons-extended-desktop"), "Apache 2.0")
            put(UnversionedArtifact("org.jetbrains.compose.material", "material-ripple-desktop"), "Apache 2.0")
            put(UnversionedArtifact("org.jetbrains.compose.runtime", "runtime-desktop"), "Apache 2.0")
            put(UnversionedArtifact("org.jetbrains.compose.runtime", "runtime-saveable-desktop"), "Apache 2.0")
            put(UnversionedArtifact("org.jetbrains.compose.ui", "ui-desktop"), "Apache 2.0")
            put(UnversionedArtifact("org.jetbrains.compose.ui", "ui-geometry-desktop"), "Apache 2.0")
            put(UnversionedArtifact("org.jetbrains.compose.ui", "ui-graphics-desktop"), "Apache 2.0")
            put(UnversionedArtifact("org.jetbrains.compose.ui", "ui-text-desktop"), "Apache 2.0")
            put(UnversionedArtifact("org.jetbrains.compose.ui", "ui-tooling-preview-desktop"), "Apache 2.0")
            put(UnversionedArtifact("org.jetbrains.compose.ui", "ui-unit-desktop"), "Apache 2.0")
            put(UnversionedArtifact("org.jetbrains.compose.ui", "ui-util-desktop"), "Apache 2.0")
            put(UnversionedArtifact("org.jetbrains.kotlin", "kotlin-stdlib"), "Apache 2.0")
            put(UnversionedArtifact("org.jetbrains.kotlin", "kotlin-stdlib-common"), "Apache 2.0")
            put(UnversionedArtifact("org.jetbrains.kotlin", "kotlin-stdlib-jdk7"), "Apache 2.0")
            put(UnversionedArtifact("org.jetbrains.kotlin", "kotlin-stdlib-jdk8"), "Apache 2.0")
            put(UnversionedArtifact("org.jetbrains.kotlinx", "kotlinx-coroutines-core-jvm"), "Apache 2.0")
            put(UnversionedArtifact("org.jetbrains.kotlinx", "kotlinx-coroutines-swing"), "Apache 2.0")
            put(UnversionedArtifact("org.jetbrains.skiko", "skiko-jvm"), "Apache 2.0")
            put(UnversionedArtifact("org.jetbrains.skiko", "skiko-jvm-runtime-linux-x64"), "Apache 2.0")
            put(UnversionedArtifact("org.jsoup", "jsoup"), "MIT")
            put(UnversionedArtifact("org.slf4j", "jul-to-slf4j"), "MIT")
            put(UnversionedArtifact("org.slf4j", "slf4j-api"), "MIT")
            put(UnversionedArtifact("org.whispersystems", "curve25519-java"), "GPL 3.0")
        }
    }

    init {
        group = "build"
    }

    @TaskAction
    @Throws(IOException::class)
    protected fun generateSource() {
        val project = project
        val packageName = configuration?.packageName
        var className = configuration?.className
        if (className == null) {
            className = "BuildData"
        }

        // Get version from Gradle project information
        val version = project.version.toString()

        // Get Git hashes, last commit time, current branch and briar-core tag using JGit.
        // First, open main git repository
        val dir = project.projectDir
        val git = Git.open(dir)
        val repository = git.repository

        // Find and open core repository
        val repositoryCore = SubmoduleWalk.getSubmoduleRepository(repository, "briar")
        val dirBriar = repositoryCore.directory
        val gitBriar = Git.open(dirBriar)

        // Get head ref and it's name => current hash
        val head = repository.resolve(Constants.HEAD)
        val gitHash = head.name

        // Get latest commit and its commit time
        val first: RevCommit = try {
            getLastCommit(git)
        } catch (e: Throwable) {
            throw GradleScriptException("Error while fetching commits", e)
        }

        // Convert from seconds to milliseconds
        val commitTime = first.commitTime * 1000L

        // Get current branch, if any
        var gitBranch = "<unknown>"
        val prefix = "refs/heads/"
        val fullBranch = repository.fullBranch
        if (fullBranch.startsWith(prefix)) {
            gitBranch = fullBranch.substring(prefix.length)
        }

        // Get head ref and it's name => current core hash
        val coreHead = repositoryCore.resolve(Constants.HEAD)
        val coreGitHash = coreHead.name

        // Get latest core tag
        val coreReleaseTag = getLastReleaseTag(gitBriar)
        val coreVersion = coreReleaseTag.name.substring("refs/tags/release-".length)

        // Get direct and transitive dependencies
        val artifacts = mutableListOf<VersionedArtifact>()
        val configuration = project.configurations.getByName("default")
        val resolved = configuration.resolvedConfiguration
        for (artifact in resolved.resolvedArtifacts) {
            if (artifact !is PreResolvedResolvableArtifact ||
                artifact.moduleVersion.id.group == "briar-desktop"
            ) {
                continue
            }
            val id = artifact.moduleVersion.id
            artifacts.add(VersionedArtifact(id.group, id.name, id.version))
        }
        artifacts.sortWith(compareBy<VersionedArtifact> { it.group }.thenBy { it.artifact })

        // Generate output file
        checkNotNull(packageName) { "Please specify 'packageName'." }
        val parts = packageName.split("\\.".toRegex()).toTypedArray()
        val pathBuildDir = project.buildDir.toPath()
        val source = Util.getSourceDir(pathBuildDir)
        var path = source
        for (i in parts.indices) {
            path = path.resolve(parts[i])
        }
        Files.createDirectories(path)
        val file = path.resolve("$className.kt")
        val content = createSource(
            packageName, className, version,
            commitTime, gitHash, gitBranch,
            coreGitHash, coreVersion, artifacts
        )
        val input: InputStream = ByteArrayInputStream(
            content.toByteArray(StandardCharsets.UTF_8)
        )
        Files.copy(input, file, StandardCopyOption.REPLACE_EXISTING)
    }

    private fun getLastCommit(git: Git): RevCommit {
        val commits = git.log().call()
        val iterator: Iterator<RevCommit> = commits.iterator()
        if (!iterator.hasNext()) {
            throw NoSuchElementException()
        }
        return iterator.next()
    }

    private fun getLastReleaseTag(git: Git): Ref {
        val tags = git.tagList().call()
        val releases = tags.filter { tag -> tag.name.startsWith("refs/tags/release-") }
        return releases[releases.size - 1]
    }

    private fun createSource(
        packageName: String,
        className: String,
        version: String,
        gitTime: Long,
        gitHash: String,
        gitBranch: String,
        coreGitHash: String,
        coreVersion: String,
        artifacts: List<VersionedArtifact>,
    ) = FileBuilder().apply {
        line("// this file is generated, do not edit")
        line("package $packageName")
        line()
        line("import org.briarproject.briar.desktop.about.Artifact")
        line()
        line("object $className {")
        line("    val VERSION = \"$version\"")
        line("    val GIT_TIME = ${gitTime}L")
        line("    val GIT_HASH = \"$gitHash\"")
        line("    val GIT_BRANCH = \"$gitBranch\"")
        line("    val CORE_HASH = \"$coreGitHash\"")
        line("    val CORE_VERSION = \"$coreVersion\"")
        line()
        line("    val ARTIFACTS: List<Artifact> = buildList {")
        for (artifact in artifacts) {
            val license = LICENSES[artifact.unversioned()]
            if (license == null) {
                logger.warn("No license defined for \"${artifact.group}:${artifact.artifact}\", please define in GenerateBuildDataSourceTask.kt")
            }
            val licenseName = license ?: "Unknown"
            line("        add(Artifact(\"${artifact.group}\", \"${artifact.artifact}\", \"${artifact.version}\", \"$licenseName\"))")
        }
        line("    }")
        line("}")
    }.toString()
}
