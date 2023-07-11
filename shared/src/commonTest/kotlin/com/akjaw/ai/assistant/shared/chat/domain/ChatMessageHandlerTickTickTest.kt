package com.akjaw.ai.assistant.shared.chat.domain

import app.cash.turbine.test
import com.akjaw.ai.assistant.database.Database
import com.akjaw.ai.assistant.shared.chat.data.api.ProductionApiFactory
import com.akjaw.ai.assistant.shared.chat.data.database.InMemoryDriverFactory
import com.akjaw.ai.assistant.shared.chat.data.database.createDatabase
import com.akjaw.ai.assistant.shared.chat.domain.model.ChatMessage
import com.akjaw.ai.assistant.shared.composition.Dependencies
import com.akjaw.ai.assistant.shared.dashboard.domain.ChatType
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class ChatMessageHandlerTickTickTest {

    private lateinit var database: Database
    private lateinit var mockKtorEngine: MockKtorEngine
    private lateinit var systemUnderTest: ChatMessageHandler

    @BeforeTest
    fun setUp() {
        mockKtorEngine = MockKtorEngine()
        val client = Dependencies.createKtorClient(mockKtorEngine.engine)
        database = createDatabase(InMemoryDriverFactory())
        systemUnderTest = ChatMessageHandler(ProductionApiFactory(client), database)
    }

    @Test
    fun `When Database has data then it is returned`() = runTest {
        database.messageEntityQueries.insert(null, "1", 1, ChatType.TickTick, true)
        database.messageEntityQueries.insert(null, "2", 2, ChatType.TickTick, false)

        val result = systemUnderTest.getMessagesForType(ChatType.TickTick)

        result.test {
            val result = awaitItem()
            result[0] shouldBe ChatMessage.User("1")
            result[1] shouldBe ChatMessage.Api.Success("2")
        }
    }

    @Test
    fun `When API returns error then Message is an Error`() = runTest {
        mockKtorEngine.apiResult = MockKtorEngine.Result.Failure("Error")

        val result = systemUnderTest.sendMessage("", ChatType.TickTick)

        result shouldBe ChatMessage.Api.Error("Error")
    }

    @Test
    fun `When API returns success then Message is Success`() = runTest {
        mockKtorEngine.apiResult = MockKtorEngine.Result.Success("Text")

        val result = systemUnderTest.sendMessage("", ChatType.TickTick)

        result shouldBe ChatMessage.Api.Success("Text")
    }

    @Test
    fun `The task is correctly sent to the API`() = runTest {
        mockKtorEngine.apiResult = MockKtorEngine.Result.Success("")

        systemUnderTest.sendMessage("message", ChatType.TickTick)

        mockKtorEngine.passedInTask shouldBe "message"
    }
}
