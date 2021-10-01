package org.briarproject.briar.desktop

fun main(args: Array<String>) = RunWithTemporaryAccount {
    getDeterministicTestDataCreator().createTestData(5, 20, 50)
}.run()
