package com.akjaw.ai.assistant.shared.chat.data.database

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.akjaw.ai.assistant.database.Database

actual class ProductionDriverFactory(private val context: Context) : DriverFactory {

    // Required to make default DriverFactor parameter working
    actual constructor() : this(throw IllegalStateException("Android Factory constructor cannot be called without parameter"))

    actual override fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(Database.Schema, context, "database.db")
    }
}

actual class InMemoryDriverFactory : DriverFactory {

    actual override fun createDriver(): SqlDriver {
        val driver: SqlDriver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        Database.Schema.create(driver)
        return driver
    }
}