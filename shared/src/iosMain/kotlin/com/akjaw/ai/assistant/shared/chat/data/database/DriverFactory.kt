package com.akjaw.ai.assistant.shared.chat.data.database

import app.cash.sqldelight.db.SqlDriver

actual class DriverFactory {

    actual fun createDriver(): SqlDriver {
        return NativeSqliteDriver(Database.Schema, "database.db")
    }
}