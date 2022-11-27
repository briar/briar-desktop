/*
 * Briar Desktop
 * Copyright (C) 2021-2022 The Briar Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
@file:Suppress("nls")

package org.briarproject.briar.desktop.testdata.conversation

import java.time.LocalDateTime.of as dt

val conversations = conversations {
    conversation {
        contactName = "Bob"
        outgoing {
            text = "Hi, Bob"
            read = true
            date = "2022-01-21 10:10:15"
        }
        incoming {
            text = "What's up?"
            read = true
            date = "2022-01-21 10:11:05"
        }
        outgoing {
            text = "Nothing much, lately. Have you seen the new Briar Desktop release?"
            read = true
            date = "2022-01-21 10:12:34"
        }
        incoming {
            text = "Oh, wow. I have to check it out! \uD83E\uDD2F"
            read = true
            date = "2022-01-21 10:13:05"
        }
        outgoing {
            images = listOf("images/do-it-now.png")
            read = true
            date = "2022-01-21 10:13:15"
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
    conversation {
        contactName = "Georgy Voronoy"
        val start = dt(2021, 3, 13, 10, 3, 1)
        outgoing {
            text = "Hey Georgy!"
            read = true
            date = start
        }
        incoming {
            text = "Good morning, Alice"
            read = true
            date = start.plusSeconds(100)
        }
        incoming {
            text = "Check out my latest diagrams"
            images = listOf("images/voronoi1.png")
            read = true
            date = start.plusSeconds(120)
        }
        outgoing {
            text = "Awesome, thanks!"
            read = true
            date = start.plusSeconds(240)
        }
        outgoing {
            text = "I've also made some"
            images = listOf(
                "images/voronoi2.png",
                "images/voronoi3.png",
                "images/voronoi4.png",
                "images/voronoi2.png",
                "images/voronoi3.png",
                "images/voronoi4.png",
            )
            read = true
            date = start.plusSeconds(250)
        }
    }
}
