package org.briarproject.briar.desktop.builddata

internal class FileBuilder {
    private val buffer = StringBuilder()

    fun line() {
        buffer.append(nl)
    }

    fun line(string: String?) {
        buffer.append(string)
        buffer.append(nl)
    }

    override fun toString(): String {
        return buffer.toString()
    }

    companion object {
        private val nl = System.getProperty("line.separator")
    }
}
