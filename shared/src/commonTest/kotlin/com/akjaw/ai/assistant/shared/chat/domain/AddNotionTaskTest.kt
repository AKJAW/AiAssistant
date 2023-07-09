package com.akjaw.ai.assistant.shared.chat.domain

import com.akjaw.ai.assistant.shared.chat.domain.model.ChatMessage
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class AddNotionTaskTest {

    private lateinit var mockKtorEngine: MockKtorEngine
    private lateinit var systemUnderTest: AddNotionTask

    @BeforeTest
    fun setUp() {
        mockKtorEngine = MockKtorEngine()
        val client = createKtorClient(mockKtorEngine.engine)
        systemUnderTest = AddNotionTask(client)
    }

    @Test
    fun `When API returns error then Message is an Error`() = runTest {
        mockKtorEngine.apiResult = MockKtorEngine.Result.Failure("Error")

        val result = systemUnderTest.execute("")

        result shouldBe ChatMessage.Api.Error("Error")
    }

    @Test
    fun `When API returns success then Message is Success`() = runTest {
        mockKtorEngine.apiResult = MockKtorEngine.Result.Success("Text")

        val result = systemUnderTest.execute("")

        result shouldBe ChatMessage.Api.Success("Text")
    }

    @Test
    fun `The task is correctly sent to the API`() = runTest {
        mockKtorEngine.apiResult = MockKtorEngine.Result.Success("")

        systemUnderTest.execute("message")

        mockKtorEngine.passedInTask shouldBe "message"
    }
}
