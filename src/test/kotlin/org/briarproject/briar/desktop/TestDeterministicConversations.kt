package org.briarproject.briar.desktop

fun main() = RunWithTemporaryAccount {
    getDeterministicTestDataCreator().createTestData(5, 20, 50)
}.run()
