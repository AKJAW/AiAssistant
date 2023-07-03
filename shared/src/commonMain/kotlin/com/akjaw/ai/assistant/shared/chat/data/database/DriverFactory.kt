package com.akjaw.ai.assistant.shared.chat.data.database

import app.cash.sqldelight.db.SqlDriver
import com.akjaw.ai.assistant.database.Database

expect class DriverFactory {

    fun createDriver(): SqlDriver
}

fun createDatabase(driverFactory: DriverFactory): Database {
    val driver = driverFactory.createDriver()

    return Database(driver)
}