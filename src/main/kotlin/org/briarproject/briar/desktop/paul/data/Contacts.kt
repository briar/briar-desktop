package org.briarproject.briar.desktop.paul.data

import org.briarproject.briar.desktop.paul.model.Contact
import org.briarproject.briar.desktop.paul.model.Message

object ContactList {
    val msg1: List<Message> = listOf<Message>(
        Message(from = "Alice", message = "hello!", time = "2 days ago", delivered = true),
        Message(from = null, message = "yes I hear you", time = "1 day ago", delivered = true),
        Message(
            from = null,
            message = "I am messaging you through this fake Briar Desktop app",
            time = "18 hrs. ago",
            delivered = true
        ),
        Message(from = "Alice", message = "Ah I see, very neat", time = "12 min. ago", delivered = true),
        Message(from = null, message = "Loren Ipsum", time = "2 min. ago", delivered = false)
    )
    val msg2: List<Message> = listOf<Message>(
        Message(
            from = null,
            message = "The air bites shrewdly; it is very cold.",
            time = "2 days ago",
            delivered = true
        ),
        Message(from = "Bob", message = "It is a nipping and an eager air.", time = "1 day ago", delivered = true),
        Message(from = null, message = "What hour now?", time = "18 hrs. ago", delivered = true),
        Message(from = "Bob", message = "I think it lacks of twelve.", time = "12 min. ago", delivered = true),
        Message(from = null, message = "No, it is struck.", time = "2 min. ago", delivered = false),
        Message(
            from = "Bob",
            message = "Indeed? I heard it not: then it draws near the season Wherein the spirit held his wont to walk. A flourish of trumpets, and ordnance shot off, within. What does this mean, my lord?",
            time = "2 min. ago",
            delivered = false
        ),
        Message(
            from = null,
            message = "The air bites shrewdly; it is very cold.",
            time = "2 days ago",
            delivered = true
        ),
        Message(from = "Bob", message = "It is a nipping and an eager air.", time = "1 day ago", delivered = true),
        Message(from = null, message = "What hour now?", time = "18 hrs. ago", delivered = true),
        Message(from = "Bob", message = "I think it lacks of twelve.", time = "12 min. ago", delivered = true),
        Message(from = null, message = "No, it is struck.", time = "2 min. ago", delivered = false),
        Message(
            from = null,
            message = "The air bites shrewdly; it is very cold.",
            time = "2 days ago",
            delivered = true
        ),
        Message(
            from = null,
            message = "The air bites shrewdly; it is very cold.",
            time = "2 days ago",
            delivered = true
        ),
        Message(from = "Bob", message = "It is a nipping and an eager air.", time = "1 day ago", delivered = true),
        Message(from = null, message = "What hour now?", time = "18 hrs. ago", delivered = true),
        Message(from = "Bob", message = "I think it lacks of twelve.", time = "12 min. ago", delivered = true),
        Message(from = null, message = "No, it is struck.", time = "2 min. ago", delivered = false),
        Message(from = "Bob", message = "It is a nipping and an eager air.", time = "1 day ago", delivered = true),
        Message(from = null, message = "What hour now?", time = "18 hrs. ago", delivered = true),
        Message(from = "Bob", message = "I think it lacks of twelve.", time = "12 min. ago", delivered = true),
        Message(from = null, message = "No, it is struck.", time = "2 min. ago", delivered = false),
        Message(
            from = "Bob",
            message = "Indeed? I heard it not: then it draws near the season Wherein the spirit held his wont to walk. A flourish of trumpets, and ordnance shot off, within. What does this mean, my lord?",
            time = "2 min. ago",
            delivered = false
        )
    )
    val msg3: List<Message> = listOf<Message>(
        Message(
            from = null,
            message = "Give him this money and these notes, Reynaldo.",
            time = "2 days ago",
            delivered = true
        ),
        Message(from = "Bob", message = "I will, my lord.", time = "1 day ago", delivered = true),
        Message(
            from = null,
            message = "You shall do marvellous wisely, good Reynaldo,Before you visit him, to make inquireOf his behavior.",
            time = "18 hrs. ago",
            delivered = true
        ),
        Message(from = "Bob", message = "My lord, I did intend it.", time = "12 min. ago", delivered = true),
        Message(
            from = null,
            message = "Marry, well said; very well said. Look you, sir,Inquire me first what Danskers are in Paris;And how, and who, what means, and where they keep,What company, at what expense; and findingBy this encompassment and drift of questionThat they do know my son, come you more nearerThan your particular demands will touch it:Take you, as ’twere, some distant knowledge of him;As thus, ‘I know his father and his friends,And in part him: ‘ do you mark this, Reynaldo?",
            time = "2 min. ago",
            delivered = false
        ),
    )
    val msg4: List<Message> = listOf<Message>(
        Message(
            from = "Bob",
            message = "So art thou to revenge, when thou shalt hear.",
            time = "2 days ago",
            delivered = true
        ),
        Message(from = null, message = "What?", time = "1 day ago", delivered = true),
        Message(
            from = "Bob",
            message = "I am your father’s spirit, doomed for a certain time to walk the night, and for the day to burn in fires, till the foul crimes done during my lifetime have been burnt and purged away. But that I am forbidden to tell the secrets of my prison-house I could tell a tale whose lightest word would shrivel up your soul, freeze your young blood, make your eyes start from their sockets and your hair stand up on end like the quills of a frightened porcupine. But this eternal torture is not for ears of flesh and blood. Listen, oh listen! If you ever loved your dear father ….",
            time = "18 hrs. ago",
            delivered = true
        ),
        Message(from = null, message = "Oh God!", time = "12 min. ago", delivered = true),
    )
    val msg5: List<Message> = listOf<Message>(
        Message(
            from = null,
            message = "Here's a knocking indeed! If a man were porter of hell-gate, he should have old turning the key.  Knocking within Knock, knock, knock! Who's there, i' the name of Beelzebub? Here's a farmer, that hanged himself on the expectation of plenty: come in time; have napkins enow about you; here you'll sweat for't.",
            time = "2 days ago",
            delivered = true
        ),
        Message(
            from = null,
            message = "Knock, knock! Who's there, in the other devil's name? Faith, here's an equivocator, that could swear in both the scales against either scale; who committed treason enough for God's sake, yet could not equivocate to heaven: O, come in, equivocator.",
            time = "1 day ago",
            delivered = true
        ),
    )
    val contacts = listOf(
        Contact(name = "Alice", online = true, profile_pic = "p1.png", last_heard = "now", privateMessages = msg2),
        Contact(
            name = "Bob",
            online = false,
            profile_pic = "p2.png",
            last_heard = "22 min. ago",
            privateMessages = msg1
        ),
        Contact(
            name = "Carl",
            online = true,
            profile_pic = "p3.png",
            last_heard = "2 hr. ago ",
            privateMessages = msg3
        ),
        Contact(name = "Dan", online = false, profile_pic = "p4.png", last_heard = "1 day ago", privateMessages = msg4),
        Contact(
            name = "Eve",
            online = false,
            profile_pic = "p5.png",
            last_heard = "3 days ago",
            privateMessages = msg5
        ),
        Contact(
            name = "Fred",
            online = false,
            profile_pic = "p2.png",
            last_heard = "22 min. ago",
            privateMessages = msg1
        ),
        Contact(
            name = "Greg",
            online = true,
            profile_pic = "p3.png",
            last_heard = "2 hr. ago ",
            privateMessages = msg3
        ),
        Contact(
            name = "Harold",
            online = false,
            profile_pic = "p4.png",
            last_heard = "1 day ago",
            privateMessages = msg4
        ),
        Contact(
            name = "Irene",
            online = false,
            profile_pic = "p5.png",
            last_heard = "3 days ago",
            privateMessages = msg5
        ),
        Contact(
            name = "Jeanne",
            online = false,
            profile_pic = "p2.png",
            last_heard = "22 min. ago",
            privateMessages = msg1
        ),
        Contact(
            name = "Karl",
            online = true,
            profile_pic = "p3.png",
            last_heard = "2 hr. ago ",
            privateMessages = msg3
        ),
        Contact(
            name = "Lorn",
            online = false,
            profile_pic = "p4.png",
            last_heard = "1 day ago",
            privateMessages = msg4
        ),
        Contact(
            name = "Meg",
            online = false,
            profile_pic = "p5.png",
            last_heard = "3 days ago",
            privateMessages = msg5
        ),
        Contact(
            name = "Nile",
            online = false,
            profile_pic = "p2.png",
            last_heard = "22 min. ago",
            privateMessages = msg1
        ),
        Contact(
            name = "Oscar",
            online = true,
            profile_pic = "p3.png",
            last_heard = "2 hr. ago ",
            privateMessages = msg3
        ),
        Contact(
            name = "Paul",
            online = false,
            profile_pic = "p4.png",
            last_heard = "1 day ago",
            privateMessages = msg4
        ),
        Contact(name = "Qi", online = false, profile_pic = "p5.png", last_heard = "3 days ago", privateMessages = msg5),
    )
}

