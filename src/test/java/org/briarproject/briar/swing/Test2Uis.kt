package org.briarproject.briar.swing

import org.apache.commons.io.FileUtils
import org.briarproject.bramble.BrambleCoreEagerSingletons
import org.briarproject.briar.BriarCoreEagerSingletons
import java.nio.file.Files
import java.nio.file.Path

fun main() {
    val dataDir1 = Files.createTempDirectory("briar")
    val dataDir2 = Files.createTempDirectory("briar")
    println(dataDir1)
    println(dataDir2)
    Runtime.getRuntime().addShutdownHook(Thread {
        FileUtils.deleteDirectory(dataDir1.toFile())
        FileUtils.deleteDirectory(dataDir2.toFile())
    })
    val app1 = app(dataDir1)
    val app2 = app(dataDir2)

    app1.getUI().startBriar()
    val testDataCreator = app1.getTestDataCreator()
    testDataCreator.createTestData(10, 50, 0, 0, 0)
    app1.getUI().startUI()

    app2.getUI().startBriar()
    app2.getUI().startUI()

    val cm1 = app1.getUI().getContactManager()
    val cm2 = app2.getUI().getContactManager()

    println(cm1.handshakeLink)
    println(cm2.handshakeLink)

    try {
        cm1.addPendingContact(cm2.handshakeLink, "contact2")
        cm2.addPendingContact(cm1.handshakeLink, "contact1")
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

private fun app(dataDir: Path): BriarOnlineSwingTestApp {
    val app = DaggerBriarOnlineSwingTestApp.builder().onlineSwingTestModule(
        OnlineSwingTestModule(
            dataDir.toFile()
        )
    ).build()
    BrambleCoreEagerSingletons.Helper.injectEagerSingletons(app)
    BriarCoreEagerSingletons.Helper.injectEagerSingletons(app)
    return app
}
