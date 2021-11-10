package org.briarproject.briar.desktop

fun main() = RunWithMultipleTemporaryAccounts(listOf("alice", "bob")) {
    forEach {
        it.getDeterministicTestDataCreator().createTestData(5, 20, 50, 10, 20)
    }
}.run()
