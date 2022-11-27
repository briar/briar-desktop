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

package org.briarproject.briar.desktop.testdata.forum

import java.time.LocalDateTime

val forums = forums {
    forum {
        name = "Briar Friends"

        val me = myself()
        val anna = member("Anna", sharedForum = true)
        val paul = member("Paul")
        val claudia = member("Claudia", sharedForum = true)

        post {
            author = anna
            text = "Hi everybody!"

            post {
                author = paul
                text = "Hey Anna! It's me, Paul \uD83D\uDC4B️"

                post {
                    author = anna
                    text = "Hi Paul! Nice to have you around!"
                }

                post {
                    author = paul
                    text = "Yes indeed. Thanks for sharing the forum with me! \uD83D\uDC99\uD83E\uDD17️️"
                }
            }

            post {
                author = me
                text = "Hi Anna! Where can I see who else is in here?"

                post {
                    author = anna
                    text = "Right now it's only Paul, you and me. " +
                        "But it's actually not possible to have a list of all members of a forum, " +
                        "since every person in the forum could share it with any of their contacts."

                    post {
                        author = me
                        text = "Oh, that's a bit unexpected!️️"
                    }

                    post {
                        author = claudia
                        text = "That's actually how Paul slipped me in here. Hello everyone!️️"

                        post {
                            author = paul
                            text = "️️\uD83D\uDE08️"
                        }
                    }
                }
            }
        }

        post {
            author = anna
            text = "What do you think about Briar?"
        }
    }

    forum {
        name = "Let's try forums"

        val me = myself()
        val brian = member("Brian", sharedForum = true)
        val claudia = member("Claudia", sharedForum = true)
        val claudia2 = member("Claudia")

        post {
            author = brian
            text = "I've just started and shared this forum using Briar Desktop, " +
                "did everything work as expected?"
            date = LocalDateTime.of(2022, 11, 28, 16, 12, 38)

            post {
                author = me
                text = "Wow nice, yes, everything seems to work!"

                post {
                    author = brian
                    text = "Perfect, that's amazing!"
                }
            }

            post {
                author = claudia
                text = "Hey people! That's so exciting that forums work on Briar Desktop now, too!"

                post {
                    author = claudia2
                    text = "And it is fully compatible with Briar Android as well! I'm writing this on my phone just now."

                    post {
                        author = me
                        text = "Oh hi, can you introduce me to your Briar account on the phone, please?"
                    }
                }
            }
        }
    }
}
