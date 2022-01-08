package org.briarproject.briar.desktop.builddata;

class FileBuilder {

	private static String nl = System.getProperty("line.separator");

	private StringBuilder buffer = new StringBuilder();

	void append(String string) {
		buffer.append(string);
	}

	void line() {
		buffer.append(nl);
	}

	void line(String string) {
		buffer.append(string);
		buffer.append(nl);
	}

	public String toString() {
		return buffer.toString();
	}

}
