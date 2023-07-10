package com.akjaw.ai.assistant.shared.chat.data.database

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.akjaw.ai.assistant.database.Database

// TODO add a file database
actual class ProductionDriverFactory : DriverFactory {

    actual override fun createDriver(): SqlDriver {
        val driver: SqlDriver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        Database.Schema.create(driver)
        return driver
    }
}

actual class InMemoryDriverFactory : DriverFactory {

     actual override fun createDriver(): SqlDriver {
        val driver: SqlDriver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        Database.Schema.create(driver)
        return driver
    }
}