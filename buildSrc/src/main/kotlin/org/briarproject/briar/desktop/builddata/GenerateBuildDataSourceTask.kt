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

import org.briarproject.briar.desktop.builddata.GitUtil.getLastCommit
import org.briarproject.briar.desktop.builddata.GitUtil.getLastTagWithPrefix
import org.briarproject.briar.desktop.builddata.GitUtil.mapCommitsToTags
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.submodule.SubmoduleWalk
import org.gradle.api.GradleException
import org.gradle.api.GradleScriptException
import org.gradle.api.InvalidUserDataException
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
            put(UnversionedArtifact("androidx.annotation", "annotation-jvm"), "Apache 2.0")
            put(UnversionedArtifact("androidx.arch.core", "core-common"), "Apache 2.0")
            put(UnversionedArtifact("androidx.collection", "collection-jvm"), "Apache 2.0")
            put(UnversionedArtifact("androidx.lifecycle", "lifecycle-common-jvm"), "Apache 2.0")
            put(UnversionedArtifact("androidx.lifecycle", "lifecycle-runtime-desktop"), "Apache 2.0")
            put(UnversionedArtifact("androidx.lifecycle", "lifecycle-viewmodel-desktop"), "Apache 2.0")
            put(UnversionedArtifact("ch.qos.logback", "logback-classic"), "EPL 1.0/LGPL 2.1")
            put(UnversionedArtifact("ch.qos.logback", "logback-core"), "EPL 1.0/LGPL 2.1")
            put(UnversionedArtifact("com.fasterxml.jackson.core", "jackson-annotations"), "Apache 2.0")
            put(UnversionedArtifact("com.fasterxml.jackson.core", "jackson-core"), "Apache 2.0")
            put(UnversionedArtifact("com.fasterxml.jackson.core", "jackson-databind"), "Apache 2.0")
            put(UnversionedArtifact("com.github.ajalt.clikt", "clikt-jvm"), "Apache 2.0")
            put(UnversionedArtifact("com.github.ajalt.clikt", "clikt-core-jvm"), "Apache 2.0")
            put(UnversionedArtifact("com.github.ajalt.colormath", "colormath-jvm"), "Apache 2.0")
            put(UnversionedArtifact("com.github.ajalt.mordant", "mordant-core-jvm"), "Apache 2.0")
            put(UnversionedArtifact("com.github.ajalt.mordant", "mordant-jvm"), "Apache 2.0")
            put(UnversionedArtifact("com.github.ajalt.mordant", "mordant-jvm-ffm-jvm"), "Apache 2.0")
            put(UnversionedArtifact("com.github.ajalt.mordant", "mordant-jvm-graal-ffi-jvm"), "Apache 2.0")
            put(UnversionedArtifact("com.github.ajalt.mordant", "mordant-jvm-jna-jvm"), "Apache 2.0")
            put(UnversionedArtifact("com.google.code.findbugs", "jsr305"), "Apache 2.0")
            put(UnversionedArtifact("com.google.dagger", "dagger"), "Apache 2.0")
            put(UnversionedArtifact("com.h2database", "h2"), "EPL 1.0/MPL 2.0")
            put(UnversionedArtifact("com.ibm.icu", "icu4j"), "ICU")
            put(UnversionedArtifact("com.rometools", "rome"), "Apache 2.0")
            put(UnversionedArtifact("com.rometools", "rome-utils"), "Apache 2.0")
            put(UnversionedArtifact("com.squareup.okhttp3", "okhttp"), "Apache 2.0")
            put(UnversionedArtifact("com.squareup.okio", "okio-jvm"), "Apache 2.0")
            put(UnversionedArtifact("de.mobanisto", "toast4j"), "MIT")
            put(UnversionedArtifact("de.jangassen", "jfa"), "Apache 2.0")
            put(UnversionedArtifact("io.github.oshai", "kotlin-logging-jvm"), "Apache 2.0")
            put(UnversionedArtifact("javax.inject", "javax.inject"), "Apache 2.0")
            put(UnversionedArtifact("net.i2p.crypto", "eddsa"), "CC0 1.0")
            put(UnversionedArtifact("net.java.dev.jna", "jna"), "Apache 2.0/LGPL 2.1")
            put(UnversionedArtifact("net.java.dev.jna", "jna-platform"), "Apache 2.0/LGPL 2.1")
            put(UnversionedArtifact("org.bitlet", "weupnp"), "LGPL 2.1")
            put(UnversionedArtifact("org.bouncycastle", "bcprov-jdk15to18"), "BouncyCastle")
            put(UnversionedArtifact("org.briarproject", "jtorctl"), "BSD 2.0")
            put(UnversionedArtifact("org.briarproject", "null-safety"), "Apache 2.0")
            put(UnversionedArtifact("org.briarproject", "socks-socket"), "Apache 2.0")
            put(UnversionedArtifact("org.briarproject", "onionwrapper-core"), "GPL 3.0")
            put(UnversionedArtifact("org.briarproject", "onionwrapper-java"), "GPL 3.0")
            put(UnversionedArtifact("org.bytedeco", "javacpp"), "Apache 2.0")
            put(UnversionedArtifact("org.jdom", "jdom2"), "JDOM (Apache without acknowledgment clause)")
            put(UnversionedArtifact("org.jetbrains", "annotations"), "Apache 2.0")
            put(
                UnversionedArtifact("org.jetbrains.androidx.lifecycle", "lifecycle-runtime-compose-desktop"),
                "Apache 2.0"
            )
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
            put(UnversionedArtifact("org.jetbrains.kotlinx", "atomicfu-jvm"), "Apache 2.0")
            put(UnversionedArtifact("org.jetbrains.kotlinx", "kotlinx-coroutines-core-jvm"), "Apache 2.0")
            put(UnversionedArtifact("org.jetbrains.kotlinx", "kotlinx-coroutines-swing"), "Apache 2.0")
            put(UnversionedArtifact("org.jetbrains.skiko", "skiko-awt"), "Apache 2.0")
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
        val windowsAumi = configuration?.windowsAumi

        checkNotNull(packageName) { "Please specify 'packageName'." }
        checkNotNull(windowsAumi) { "Please specify 'windowsAumi'." }

        // Get version from Gradle project information
        val version = project.version.toString()

        // Get Git hashes, last commit time, current branch and briar-core tag using JGit.
        // First, open main git repository
        val dir = project.rootProject.projectDir
        val git = Git.open(dir)
        val repository = git.repository
        val status = git.status().call()

        // Find and open core repository
        val repositoryCore = SubmoduleWalk.getSubmoduleRepository(repository, "briar")
        val dirBriar = repositoryCore.directory
        val gitBriar = Git.open(dirBriar)

        // Get head ref and its name => current hash
        val head = repository.resolve(Constants.HEAD)
        val gitHash = head.name + if (status.hasUncommittedChanges()) "-dirty" else ""

        // Get latest commit and its commit time
        val first: RevCommit = try {
            getLastCommit(git)
        } catch (e: Throwable) {
            throw GradleScriptException("Error while fetching commits", e)
        }

        // Convert from seconds to milliseconds
        val commitTime = first.commitTime * 1000L

        // Get current branch, if any
        var gitBranch: String? = null
        val prefix = "refs/heads/"
        val fullBranch = repository.fullBranch
        if (fullBranch.startsWith(prefix)) {
            gitBranch = fullBranch.substring(prefix.length)
        }

        val commitToTags = mapCommitsToTags(git)

        // Get current tag, if any
        var gitTag: String? = null
        val prefixTags = "refs/tags/"
        val tags = commitToTags[first.name]
        // NOTE: we do not break ties here yet. Currently, we do not have a practice of
        //   creating multiple tags pointing to the same commit as we do in briar core.
        tags.forEach { tag ->
            tag.name.startsWith(prefixTags)
            gitTag = tag.name.substring(prefixTags.length)
            return@forEach
        }
        if (gitTag == null) {
            project.logger.lifecycle("No tag found")
        } else {
            project.logger.lifecycle("Found tag $gitTag")
        }

        // Get head ref and its name => current core hash
        val coreHead = repositoryCore.resolve(Constants.HEAD)
        val coreGitHash = coreHead.name

        val coreCommitToTags = mapCommitsToTags(gitBriar)

        // Get latest core tag
        val tagPrefix = "release"
        val coreReleaseTag = getLastTagWithPrefix(gitBriar, coreCommitToTags, tagPrefix)
            ?: throw GradleException("Unable to determine last core release tag")
        val coreVersion = coreReleaseTag.name.substring("refs/tags/$tagPrefix-".length) + " ($tagPrefix)"
        project.logger.lifecycle("Found briar core version $coreVersion")

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
            commitTime, gitHash, gitBranch, gitTag,
            coreGitHash, coreVersion, artifacts,
            windowsAumi
        )
        val input: InputStream = ByteArrayInputStream(
            content.toByteArray(StandardCharsets.UTF_8)
        )
        Files.copy(input, file, StandardCopyOption.REPLACE_EXISTING)
    }

    private fun createSource(
        packageName: String,
        className: String,
        version: String,
        gitTime: Long,
        gitHash: String,
        gitBranch: String?,
        gitTag: String?,
        coreGitHash: String,
        coreVersion: String,
        artifacts: List<VersionedArtifact>,
        windowsAumi: String,
    ) = FileBuilder().apply {
        val branch = if (gitBranch == null) "null" else "\"$gitBranch\""
        val tag = if (gitTag == null) "null" else "\"$gitTag\""
        line("// this file is generated, do not edit")
        line("package $packageName")
        line()
        line("import org.briarproject.briar.desktop.about.Artifact")
        line()
        line("object $className {")
        line("    val VERSION = \"$version\"")
        line("    val GIT_TIME = ${gitTime}L")
        line("    val GIT_HASH = \"$gitHash\"")
        line("    val GIT_BRANCH: String? = $branch")
        line("    val GIT_TAG: String? = $tag")
        line("    val CORE_HASH = \"$coreGitHash\"")
        line("    val CORE_VERSION = \"$coreVersion\"")
        line("    val WINDOWS_AUMI = \"$windowsAumi\"")
        line()
        line("    val ARTIFACTS: List<Artifact> = buildList {")
        var missingLicenses = false
        for (artifact in artifacts) {
            val license = LICENSES[artifact.unversioned()]
            if (license == null) {
                logger.warn("No license defined for \"${artifact.group}:${artifact.artifact}\", please define in GenerateBuildDataSourceTask.kt")
                missingLicenses = true
            }
            val licenseName = license ?: "Unknown"
            line("        add(Artifact(\"${artifact.group}\", \"${artifact.artifact}\", \"${artifact.version}\", \"$licenseName\"))")
        }
        if (missingLicenses) {
            throw InvalidUserDataException("Some dependencies don't have their licenses defined. See the log for details.")
        }
        line("    }")
        line("}")
    }.toString()
}
