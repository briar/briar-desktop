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
