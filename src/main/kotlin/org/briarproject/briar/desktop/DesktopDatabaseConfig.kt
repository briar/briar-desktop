package org.briarproject.briar.desktop

import org.briarproject.bramble.api.crypto.KeyStrengthener
import org.briarproject.bramble.api.db.DatabaseConfig
import java.io.File
import java.nio.file.Path

internal class DesktopDatabaseConfig(private val dbDir: Path, private val keyDir: Path) :
    DatabaseConfig {

    override fun getDatabaseDirectory(): File = dbDir.toFile()

    override fun getDatabaseKeyDirectory(): File = keyDir.toFile()

    override fun getKeyStrengthener(): KeyStrengthener? = null
}
