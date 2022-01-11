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

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.plugins.JavaPluginExtension

class BuildDataPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val logger = project.logger
        logger.info("applying version access plugin")

        val extension = project.extensions.create(
            "buildData", BuildDataPluginExtension::class.java
        )
        val task = project.tasks.create(
            "buildData", GenerateBuildDataSourceTask::class.java
        )
        task.configuration = extension
        project.tasks.findByName("compileJava")!!.dependsOn(task)
        val pathBuildDir = project.buildDir.toPath()
        val source = Util.getSourceDir(pathBuildDir)
        val sourceSets = project.extensions.getByType(JavaPluginExtension::class.java).sourceSets.findByName("main")
        sourceSets!!.java { sourceSet: SourceDirectorySet -> sourceSet.srcDir(source) }
    }
}
