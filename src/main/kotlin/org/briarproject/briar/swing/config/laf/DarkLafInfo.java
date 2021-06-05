package org.briarproject.briar.swing.config.laf;

import com.github.weisj.darklaf.theme.Theme;

public class DarkLafInfo implements LafInfo {

	private final String name;
	private final Theme theme;

	public DarkLafInfo(String name, Theme theme) {
		this.name = name;
		this.theme = theme;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getId() {
		return theme.getClass().getName();
	}

	public Theme getTheme() {
		return theme;
	}

}
