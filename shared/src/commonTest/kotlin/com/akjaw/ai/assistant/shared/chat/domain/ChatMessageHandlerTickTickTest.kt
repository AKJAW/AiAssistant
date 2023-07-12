package com.akjaw.ai.assistant.shared.chat.domain

import com.akjaw.ai.assistant.shared.chat.domain.model.ChatMessage
import com.akjaw.ai.assistant.shared.chat.helpers.createChatMessageHandler
import com.akjaw.ai.assistant.shared.composition.Dependencies
import com.akjaw.ai.assistant.shared.dashboard.domain.ChatType
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class ChatMessageHandlerTickTickTest {

    private lateinit var mockKtorEngine: MockKtorEngine
    private lateinit var systemUnderTest: ChatMessageHandler

    @BeforeTest
    fun setUp() {
        mockKtorEngine = MockKtorEngine()
        systemUnderTest = createChatMessageHandler(engine = mockKtorEngine.engine)
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
