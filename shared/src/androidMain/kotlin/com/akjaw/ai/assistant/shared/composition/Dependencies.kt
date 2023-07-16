package com.akjaw.ai.assistant.shared.composition

import com.akjaw.ai.assistant.shared.chat.data.database.DriverFactory
import com.akjaw.ai.assistant.shared.chat.data.database.createDatabase

actual fun initializeDependencies(driverFactory: DriverFactory) {
    createDatabase(driverFactory)
}
