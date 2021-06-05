package org.briarproject.briar.swing.dialogs;

import org.apache.commons.io.IOUtils;
import org.briarproject.briar.swing.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Window;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.text.html.HTMLEditorKit;

import de.topobyte.melon.resources.Resources;

public class AboutDialog extends JDialog {

	private static final long serialVersionUID = -6673051400374126614L;

	final static Logger logger = LoggerFactory.getLogger(AboutDialog.class);

	private JEditorPane pane;

	public AboutDialog(Window owner) {
		super(owner, "About Briar Swing");
		JScrollPane jsp = new JScrollPane();
		setContentPane(jsp);

		pane = new JEditorPane();
		jsp.setViewportView(pane);
		pane.setEditable(false);

		HTMLEditorKit kit = new HTMLEditorKit();
		pane.setEditorKit(kit);

		try {
			setup();
		} catch (IOException e) {
			logger.error("unable to setup page", e);
		}
	}

	private void setup() throws IOException {
		String filenameLogo = "briar_logo_large.png";
		Path fileLogo = Files.createTempFile("briar-swing", ".png");
		fileLogo.toFile().deleteOnExit();
		try (InputStream logo = Resources
				.stream("res/images/" + filenameLogo)) {
			Files.copy(logo, fileLogo, StandardCopyOption.REPLACE_EXISTING);
		}

		String filename = "res/help/about.html";
		InputStream input = Resources.stream(filename);

		String html = IOUtils.toString(input);
		html = html.replace("VERSIONCODE", Version.getVersion());
		html = html.replace("briar_logo_large.png", fileLogo.toString());

		Path file = Files.createTempFile("briar-swing", ".html");
		file.toFile().deleteOnExit();
		BufferedWriter writer = Files.newBufferedWriter(file);
		IOUtils.write(html, writer);
		writer.close();

		URL url = file.toUri().toURL();

		logger.debug("url: " + url);
		pane.setPage(url);
	}

}