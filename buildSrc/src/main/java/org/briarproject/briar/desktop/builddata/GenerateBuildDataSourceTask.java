package org.briarproject.briar.desktop.builddata;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.gradle.api.GradleScriptException;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskAction;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Iterator;
import java.util.NoSuchElementException;

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

		/*
		 * Get version from Gradle project information
		 */
		String version = project.getVersion().toString();

		/*
		 * Get Git hash and last commit time using JGit
		 */
		// Open git repository
		File dir = project.getProjectDir();
		Git git = Git.open(dir);
		Repository repository = git.getRepository();

		// Get head ref and it's name => current hash
		ObjectId head = repository.resolve(Constants.HEAD);
		String gitHash = head.getName();

		// Get latest commit and its commit time
		RevCommit first;
		try {
			first = getLastCommit(git);
		} catch (GitAPIException | NoSuchElementException e) {
			throw new GradleScriptException("Error while fetching commits", e);
		}

		// Convert from seconds to milliseconds
		long commitTime = first.getCommitTime() * 1000L;

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

		String content = createSource(packageName, className, version,
				commitTime, gitHash);

		InputStream in = new ByteArrayInputStream(
				content.getBytes(StandardCharsets.UTF_8));
		Files.copy(in, file, StandardCopyOption.REPLACE_EXISTING);
	}

	private RevCommit getLastCommit(Git git) throws GitAPIException {
		Iterable<RevCommit> commits = git.log().call();

		Iterator<RevCommit> iterator = commits.iterator();
		if (!iterator.hasNext()) {
			throw new NoSuchElementException();
		}
		return iterator.next();
	}

	private String createSource(String packageName, String className,
			String version, long gitTime, String gitHash) {
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
		// public static long getGitTime() {
		//		return 1641645802088L;
		// }
		buffer.line("    public static long getGitTime() {");
		buffer.line("        return " + gitTime + "L;");
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
