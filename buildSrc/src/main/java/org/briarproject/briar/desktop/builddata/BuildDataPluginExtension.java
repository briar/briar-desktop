package org.briarproject.briar.desktop.builddata;

import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;

public class BuildDataPluginExtension {

	@Input
	private String packageName;
	@Input
	@Optional
	private String className;

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

}
