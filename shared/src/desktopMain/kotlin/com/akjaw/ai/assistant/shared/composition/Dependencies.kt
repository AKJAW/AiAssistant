package com.akjaw.ai.assistant.shared.composition

import com.akjaw.ai.assistant.shared.chat.data.database.DriverFactory
import com.akjaw.ai.assistant.shared.chat.data.database.ProductionDriverFactory
import com.akjaw.ai.assistant.shared.chat.data.database.createDatabase

fun initializeDependencies() {
    initializeDependencies(ProductionDriverFactory())
}

actual fun initializeDependencies(driverFactory: DriverFactory) {
    createDatabase(driverFactory)
}
