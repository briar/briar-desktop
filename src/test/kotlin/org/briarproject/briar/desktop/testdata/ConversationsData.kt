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
        contactName = "Polonius"
        val start = dt(2019, 2, 24, 22, 22)
        incoming {
            text = "My lord, the queen would speak with you, and presently."
            read = true
            date = start
        }
        outgoing {
            text = "Do you see yonder cloud that's almost in shape of a camel?"
            read = true
            date = start.plusSeconds(100)
        }
        incoming {
            text = "By the mass, and 'tis like a camel, indeed."
            read = true
            date = start.plusSeconds(300)
        }
        outgoing {
            text = "Methinks it is like a weasel."
            read = true
            date = start.plusSeconds(400)
        }
        incoming {
            text = "It is backed like a weasel."
            read = true
            date = start.plusSeconds(500)
        }
        outgoing {
            text = "Or like a whale?"
            read = true
            date = start.plusSeconds(550)
        }
        incoming {
            text = "Very like a whale."
            read = true
            date = start.plusSeconds(600)
        }
        outgoing {
            text = "Then I will come to my mother by and by. They fool me to the top of my bent. I will come by and by."
            read = true
            date = start.plusSeconds(620)
        }
        incoming {
            text = "I will say so."
            read = true
            date = start.plusSeconds(680)
        }
        outgoing {
            text = "By and by is easily said."
            read = true
            date = start.plusSeconds(777)
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
