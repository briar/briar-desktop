package org.briarproject.briar.swing.dialogs

import kotlin.system.exitProcess

fun main() {
    AddContactPrompt.promptForLink(
        null,
        "briar://abcdefghijklmnopqrstuvwxyz1234567890abcdefghijklmnopq"
    );
    exitProcess(0);
}