package com.akjaw.ai.assistant.shared.chat.data.database

import android.content.Context
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.akjaw.ai.assistant.database.Database

actual class DriverFactory(private val context: Context) {

    actual fun createDriver(): SqlDriver {
        return AndroidSqliteDriver(schema = Database.Schema, context, "test.db")
    }
}