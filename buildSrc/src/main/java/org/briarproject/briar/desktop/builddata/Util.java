package org.briarproject.briar.desktop.builddata;

import java.nio.file.Path;

class Util {

	static Path getSourceDir(Path pathBuildDir) {
		return pathBuildDir.resolve("generatedBuildData");
	}

}
