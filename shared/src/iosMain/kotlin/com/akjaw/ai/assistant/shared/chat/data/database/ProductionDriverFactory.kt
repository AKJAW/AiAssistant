package com.akjaw.ai.assistant.shared.chat.data.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.native.NativeSqliteDriver
import app.cash.sqldelight.driver.native.inMemoryDriver
import com.akjaw.ai.assistant.database.Database

actual class ProductionDriverFactory : DriverFactory {

    actual override fun createDriver(): SqlDriver {
        return NativeSqliteDriver(Database.Schema, "database.db")
    }
}

actual class InMemoryDriverFactory : DriverFactory {

    actual override fun createDriver(): SqlDriver {
        return inMemoryDriver(Database.Schema)
    }
}