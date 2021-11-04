package org.briarproject.briar.desktop

fun main() = RunWithTemporaryAccount {
    getDeterministicTestDataCreator().createTestData(5, 20, 50)
    getTestDataCreator().createTestData(5, 20, 50, 4, 4, 10)
}.run()
