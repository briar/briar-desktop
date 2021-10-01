package org.briarproject.briar.desktop.testdata

import java.time.LocalDateTime.of as dt

val conversations = conversations {
    conversation {
        contactName = "Bob"
        outgoing {
            text = "Hi, Bob"
            read = true
            date = "2020-12-24 20:10:15"
        }
        incoming {
            text = "What's up?"
            read = true
            date = "2020-12-24 20:11:05"
        }
        outgoing {
            text = "Nothing much, lately"
            read = false
            date = "2020-12-24 20:12:34"
        }
    }
    conversation {
        contactName = "Chuck"
        val start = dt(2020, 1, 12, 19, 43, 17)
        outgoing {
            text = "Hey Chuck!"
            read = true
            date = start
        }
        incoming {
            text = "Good evening, Alice"
            read = true
            date = start.plusSeconds(100)
        }
    }
    conversation {
        contactName = "Dan"
        outgoing {
            text = "Welcome to Briar!"
            read = true
            date = "2019-02-13 13:15:00"
        }
    }
}

fun main(args: Array<String>) {
    for (conversation in conversations.persons) {
        println("conversation with: ${conversation.name}")
        for (message in conversation.messages) {
            println("  ${message.direction} ${message.text} ${message.read} ${message.date}")
        }
    }
}
