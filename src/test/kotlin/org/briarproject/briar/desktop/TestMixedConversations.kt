package org.briarproject.briar.desktop

fun main(args: Array<String>) = RunWithTemporaryAccount {
    getDeterministicTestDataCreator().createTestData(5, 20, 50)
    getTestDataCreator().createTestData(5, 20, 50, 4, 4, 10)
}.run()
