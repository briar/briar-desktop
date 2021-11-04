package org.briarproject.briar.desktop

fun main() = RunWithTemporaryAccount {
    getTestDataCreator().createTestData(5, 20, 50, 4, 4, 10)
}.run()
