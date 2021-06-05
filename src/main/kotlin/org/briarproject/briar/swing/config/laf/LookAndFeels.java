package org.briarproject.briar.swing.config.laf;

import com.github.weisj.darklaf.theme.DarculaTheme;
import com.github.weisj.darklaf.theme.IntelliJTheme;

import java.util.ArrayList;
import java.util.List;

import javax.swing.UIManager;

public class LookAndFeels {

	public static List<LafInfo> AVAILABLE;

	static {
		UIManager.LookAndFeelInfo[] lafsA =
				UIManager.getInstalledLookAndFeels();
		AVAILABLE = new ArrayList<>();
		AVAILABLE.add(new NoLafInfo());
		for (UIManager.LookAndFeelInfo lookAndFeelInfo : lafsA) {
			AVAILABLE.add(new SystemLafInfo(lookAndFeelInfo));
		}
		AVAILABLE.add(new DarkLafInfo("IntelliJ", new IntelliJTheme()));
		AVAILABLE.add(new DarkLafInfo("Darcula", new DarculaTheme()));
	}

}
