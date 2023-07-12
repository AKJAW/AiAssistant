package com.akjaw.ai.assistant.shared.chat.helpers

import com.akjaw.ai.assistant.database.Database
import com.akjaw.ai.assistant.shared.chat.data.api.ProductionApiFactory
import com.akjaw.ai.assistant.shared.chat.data.database.InMemoryDriverFactory
import com.akjaw.ai.assistant.shared.chat.data.database.createDatabase
import com.akjaw.ai.assistant.shared.chat.domain.ChatMessageHandler
import com.akjaw.ai.assistant.shared.chat.domain.MockKtorEngine
import com.akjaw.ai.assistant.shared.composition.Dependencies
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.mock.MockEngine

fun createChatMessageHandler(
    database: Database = createDatabase(InMemoryDriverFactory()),
    engine: HttpClientEngine = MockKtorEngine().engine
) =
    ChatMessageHandler(ProductionApiFactory(Dependencies.createKtorClient(engine)), database)