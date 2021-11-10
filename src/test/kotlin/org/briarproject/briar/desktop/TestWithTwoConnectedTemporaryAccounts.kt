package org.briarproject.briar.desktop

import org.briarproject.briar.desktop.TestUtils.connectAll

fun main() = RunWithMultipleTemporaryAccounts(listOf("alice", "bob")) {
    forEach {
        it.getDeterministicTestDataCreator().createTestData(5, 20, 50, 10, 20)
    }
    connectAll()
}.run()
