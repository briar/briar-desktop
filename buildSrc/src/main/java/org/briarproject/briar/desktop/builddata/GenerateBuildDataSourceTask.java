package org.briarproject.briar.desktop.builddata;

import org.gradle.api.Project;
import org.gradle.api.tasks.TaskAction;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class GenerateBuildDataSourceTask extends AbstractBuildDataTask {

	public GenerateBuildDataSourceTask() {
		setGroup("build");
	}

	@TaskAction
	protected void generateSource() throws IOException {
		Project project = getProject();

		String packageName = configuration.getPackageName();
		String className = configuration.getClassName();
		if (className == null) {
			className = "BuildData";
		}

		String version = project.getVersion().toString();
		long buildTime = System.currentTimeMillis();
		// TODO: fetch correct current git hash using JGit in a platform-independent way
		String gitHash = "abcdefgh";

		if (packageName == null) {
			throw new IllegalStateException("Please specify 'packageName'.");
		}

		String[] parts = packageName.split("\\.");

		Path pathBuildDir = project.getBuildDir().toPath();
		Path source = Util.getSourceDir(pathBuildDir);

		Path path = source;
		for (int i = 0; i < parts.length; i++) {
			path = path.resolve(parts[i]);
		}

		Files.createDirectories(path);
		Path file = path.resolve(className + ".java");

		String content =
				createSource(packageName, className, version, buildTime,
						gitHash);

		InputStream in = new ByteArrayInputStream(
				content.getBytes(StandardCharsets.UTF_8));
		Files.copy(in, file, StandardCopyOption.REPLACE_EXISTING);
	}

	private String createSource(String packageName, String className,
			String version, long timestamp, String gitHash) {
		FileBuilder buffer = new FileBuilder();
		// // this file is generated, do not edit
		// package org.briarproject.briar.desktop;
		//
		// public class BuildData {
		buffer.line("// this file is generated, do not edit");
		buffer.line("package " + packageName + ";");
		buffer.line();
		buffer.line("public class " + className + " {");
		buffer.line();
		// public static String getVersion() {
		//     return "0.1";
		// }
		buffer.line("    public static String getVersion() {");
		buffer.line("        return \"" + version + "\";");
		buffer.line("    }");
		// public static long getBuildTime() {
		//		return 1641645802088L;
		// }
		buffer.line("    public static long getBuildTime() {");
		buffer.line("        return " + timestamp + "L;");
		buffer.line("    }");
		// public static long getGitHash() {
		//		return "749dda081c3e7862050255817bc239b9255b1582";
		// }
		buffer.line("    public static String getGitHash() {");
		buffer.line("        return \"" + gitHash + "\";");
		buffer.line("    }");
		buffer.line();
		buffer.line("}");

		return buffer.toString();
	}

}
