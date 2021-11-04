package org.briarproject.briar.desktop

fun main() = RunWithMultipleTemporaryAccounts(listOf("alice", "bob")) {
    forEach {
        it.getDeterministicTestDataCreator().createTestData(5, 20, 50)
    }
    val app1 = get(0)
    val app2 = get(1)
    app1.getContactManager().addPendingContact(app2.getContactManager().handshakeLink, "bob")
    app2.getContactManager().addPendingContact(app1.getContactManager().handshakeLink, "alice")
}.run()
