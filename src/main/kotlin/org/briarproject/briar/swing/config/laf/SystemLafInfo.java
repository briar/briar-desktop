package org.briarproject.briar.swing.config.laf;

import javax.swing.UIManager.LookAndFeelInfo;

public class SystemLafInfo implements LafInfo {

	private final String name;
	private final String className;

	public SystemLafInfo(LookAndFeelInfo lookAndFeelInfo) {
		name = lookAndFeelInfo.getName();
		className = lookAndFeelInfo.getClassName();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getId() {
		return className;
	}

	public String getClassName() {
		return className;
	}

}
