package org.briarproject.briar.desktop.builddata;

import org.gradle.api.internal.ConventionTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.tasks.Nested;

public abstract class AbstractBuildDataTask extends ConventionTask {

	protected final Logger logger = getLogger();

	@Nested
	protected BuildDataPluginExtension configuration;

	public BuildDataPluginExtension getConfiguration() {
		return configuration;
	}

	public void setConfiguration(BuildDataPluginExtension configuration) {
		this.configuration = configuration;
	}

}
