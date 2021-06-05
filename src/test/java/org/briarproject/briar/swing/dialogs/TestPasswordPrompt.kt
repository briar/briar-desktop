package org.briarproject.briar.swing.dialogs

import kotlin.system.exitProcess

fun main() {
    PasswordPrompt.promptForPassword();
    exitProcess(0);
}