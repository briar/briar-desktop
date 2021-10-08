package org.briarproject.briar.desktop.testdata

fun main(args: Array<String>) {
    for (conversation in conversations.persons) {
        println("conversation with: ${conversation.name}")
        for (message in conversation.messages) {
            println("  ${message.direction} ${message.text} ${message.read} ${message.date}")
        }
    }
}
