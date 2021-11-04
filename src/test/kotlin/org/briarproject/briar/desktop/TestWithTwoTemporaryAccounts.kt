package org.briarproject.briar.desktop

fun main(args: Array<String>) = RunWithMultipleTemporaryAccounts(listOf("alice", "bob")) {
    forEach {
        it.getDeterministicTestDataCreator().createTestData(5, 20, 50)
    }
}.run()
