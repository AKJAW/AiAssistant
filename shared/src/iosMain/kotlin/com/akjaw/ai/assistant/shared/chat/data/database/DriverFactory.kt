package com.akjaw.ai.assistant.shared.chat.data.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import com.akjaw.ai.assistant.database.Database

actual class DriverFactory {

    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(Database.Schema, "database.db")
    }
}