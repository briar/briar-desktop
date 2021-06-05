package org.briarproject.briar.swing.dialogs

import kotlin.system.exitProcess

fun main() {
    NewAccountPrompt.promptForDetails();
    exitProcess(0);
}