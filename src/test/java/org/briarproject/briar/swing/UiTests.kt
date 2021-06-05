package org.briarproject.briar.swing;

import org.apache.commons.io.FileUtils
import org.briarproject.bramble.BrambleCoreEagerSingletons
import org.briarproject.briar.BriarCoreEagerSingletons
import java.nio.file.Files

class UiTests {

    companion object {

        @JvmStatic
        fun runApp() {
            val dataDir = Files.createTempDirectory("briar")
            println(dataDir)
            Runtime.getRuntime().addShutdownHook(Thread {
                FileUtils.deleteDirectory(dataDir.toFile());
            })
            val app =
                DaggerBriarSwingTestApp.builder().swingTestModule(
                    SwingTestModule(
                        dataDir.toFile()
                    )
                )
                    .build()
            BrambleCoreEagerSingletons.Helper.injectEagerSingletons(app)
            BriarCoreEagerSingletons.Helper.injectEagerSingletons(app)

            app.getUI().startBriar()

            val testDataCreator = app.getTestDataCreator()
            testDataCreator.createTestData(10, 50, 0, 0, 0)

            app.getUI().startUI()
        }

    }

}
