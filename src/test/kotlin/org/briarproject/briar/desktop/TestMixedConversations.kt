package org.briarproject.briar.desktop

fun main() = RunWithTemporaryAccount {
    getDeterministicTestDataCreator().createTestData(5, 20, 50, 10, 20)
    getTestDataCreator().createTestData(5, 20, 50, 4, 10, 10)
}.run()
