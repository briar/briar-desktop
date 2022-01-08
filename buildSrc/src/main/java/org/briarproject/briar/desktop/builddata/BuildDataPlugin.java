package org.briarproject.briar.desktop.builddata;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.SourceSet;

import java.nio.file.Path;

public class BuildDataPlugin implements Plugin<Project> {

	@Override
	public void apply(final Project project) {
		Logger logger = project.getLogger();
		logger.info("applying version access plugin");

		BuildDataPluginExtension extension = project.getExtensions().create(
				"buildData", BuildDataPluginExtension.class);

		GenerateBuildDataSourceTask task = project.getTasks().create(
				"buildData", GenerateBuildDataSourceTask.class);
		task.setConfiguration(extension);

		project.getTasks().findByName("compileJava").dependsOn(task);

		Path pathBuildDir = project.getBuildDir().toPath();
		Path source = Util.getSourceDir(pathBuildDir);

		SourceSet sourceSets = project.getConvention()
				.getPlugin(JavaPluginConvention.class).getSourceSets()
				.findByName("main");
		sourceSets.java(sourceSet -> {
			sourceSet.srcDir(source);
		});
	}

}
