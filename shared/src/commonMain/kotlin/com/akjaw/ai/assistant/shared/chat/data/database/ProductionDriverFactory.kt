package com.akjaw.ai.assistant.shared.chat.data.database

import app.cash.sqldelight.EnumColumnAdapter
import app.cash.sqldelight.db.SqlDriver
import com.akjaw.ai.assistant.database.Database
import com.akjaw.ai.assistant.database.MessageEntity

interface DriverFactory {

    fun createDriver(): SqlDriver
}

expect class ProductionDriverFactory() : DriverFactory {

    override fun createDriver(): SqlDriver
}

expect class InMemoryDriverFactory() : DriverFactory {

    override fun createDriver(): SqlDriver
}

fun createDatabase(driverFactory: DriverFactory = ProductionDriverFactory()): Database {
    val driver = driverFactory.createDriver()

    return Database(driver, MessageEntityAdapter = MessageEntity.Adapter(EnumColumnAdapter()))
}