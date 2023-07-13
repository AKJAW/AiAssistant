package com.akjaw.ai.assistant.shared.chat.helpers

import com.akjaw.ai.assistant.database.Database
import com.akjaw.ai.assistant.shared.chat.data.api.ProductionApiFactory
import com.akjaw.ai.assistant.shared.chat.data.database.InMemoryDriverFactory
import com.akjaw.ai.assistant.shared.chat.data.database.createDatabase
import com.akjaw.ai.assistant.shared.chat.data.time.TimestampProvider
import com.akjaw.ai.assistant.shared.chat.domain.ChatMessageHandler
import com.akjaw.ai.assistant.shared.chat.domain.MockKtorEngine
import com.akjaw.ai.assistant.shared.chat.domain.PersistedApiChatMessageHandler
import com.akjaw.ai.assistant.shared.chat.presentation.ChatScreenStateHolder
import com.akjaw.ai.assistant.shared.composition.Dependencies
import com.akjaw.ai.assistant.shared.dashboard.domain.ChatType
import io.ktor.client.engine.HttpClientEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlin.coroutines.CoroutineContext

fun createPersistedApiChatMessageHandler(
    database: Database = createDatabase(InMemoryDriverFactory()),
    engine: HttpClientEngine = MockKtorEngine().engine,
    timestampProvider: TimestampProvider = StubTimestampProvider()
) = PersistedApiChatMessageHandler(
        ProductionApiFactory(Dependencies.createKtorClient(engine)),
        database,
        timestampProvider
    )

fun createChatScreenStateHolder(
    type: ChatType,
    coroutineContext: CoroutineContext = UnconfinedTestDispatcher(),
    chatMessageHandler: ChatMessageHandler = FakeChatMessageHandler()
) = ChatScreenStateHolder(
    type = type,
    coroutineScope = CoroutineScope(coroutineContext),
    chatMessageHandler = chatMessageHandler,
)
