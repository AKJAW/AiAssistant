package com.akjaw.ai.assistant.shared.chat.presentation

import com.akjaw.ai.assistant.shared.chat.domain.model.ChatMessage
import com.akjaw.ai.assistant.shared.chat.helpers.FakeChatMessageHandler
import com.akjaw.ai.assistant.shared.chat.helpers.createChatScreenStateHolder
import com.akjaw.ai.assistant.shared.dashboard.domain.ChatType
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlin.test.BeforeTest
import kotlin.test.Test

class ChatScreenStateHolderTest {

    private val type = ChatType.Notion
    private lateinit var fakeChatMessageHandler: FakeChatMessageHandler
    private lateinit var unconfinedTestDispatcher: TestDispatcher
    private lateinit var systemUnderTest: ChatScreenStateHolder

    @BeforeTest
    fun setup() {
        fakeChatMessageHandler = FakeChatMessageHandler()
        unconfinedTestDispatcher = UnconfinedTestDispatcher()
        systemUnderTest = createChatScreenStateHolder(
            type = type,
            coroutineContext = unconfinedTestDispatcher,
            chatMessageHandler = fakeChatMessageHandler
        )
        systemUnderTest.updateUserMessage("irrelevant")
    }

    @Test
    fun `When initialized the messages are populated by the handler`() {
        val messages = listOf(ChatMessage.Api.Success("Wow"), ChatMessage.Api.Success("Cool"))
        fakeChatMessageHandler.setSavedMessages(type, messages)
        val systemUnderTest = createChatScreenStateHolder(
            type = type,
            chatMessageHandler = fakeChatMessageHandler
        )

        systemUnderTest.messages shouldBe messages
    }

    @Test
    fun `When updating message then the state reflects it`() {
        val userMessage = "Why is there no Basil?"

        systemUnderTest.updateUserMessage(userMessage)

        systemUnderTest.userMessage shouldBe userMessage
    }

    @Test
    fun `Initially count is 0`() {
        sendMessageAndAdvance()

        systemUnderTest.count shouldBe 0
    }

    @Test
    fun `When updating user message then the count is updated to reflect its length`() {
        val userMessage = "12345"

        systemUnderTest.updateUserMessage(userMessage)

        systemUnderTest.count shouldBe 5
    }

    @Test
    fun `Initially loading is false`() {
        systemUnderTest.isLoading shouldBe false
    }

    @Test
    fun `When sending a blank message then messages are not updated`() {
        systemUnderTest.updateUserMessage(" ")

        systemUnderTest.sendMessage()

        systemUnderTest.messages shouldBe emptyList()
    }

    @Test
    fun `When sending a message starts then loading is set to true`() {
        systemUnderTest.sendMessage()

        systemUnderTest.isLoading shouldBe true
    }

    @Test
    fun `When sending a message completes then loading is set to false`() {
        sendMessageAndAdvance()

        systemUnderTest.isLoading shouldBe false
    }

    @Test
    fun `When sending a message starts then messages are updated with the user message`() {
        val userMessage = "I need basil"
        systemUnderTest.updateUserMessage(userMessage)

        systemUnderTest.sendMessage()

        systemUnderTest.messages shouldBe listOf(ChatMessage.User(userMessage))
    }

    @Test
    fun `When sending a message completes then messages are updated with the response`() {
        val message = ChatMessage.Api.Success("Api")
        fakeChatMessageHandler.responseMessage = message

        sendMessageAndAdvance()

        systemUnderTest.messages.last() shouldBe message
    }

    @Test
    fun `When sending a message starts then the user message is reset`() {
        sendMessageAndAdvance()

        systemUnderTest.userMessage shouldBe ""
    }

    @Test
    fun `When retrying an Error message starts then loading is set to true`() {
        fakeChatMessageHandler.responseMessage = ChatMessage.Api.Error("irrelevant")
        sendMessageAndAdvance()

        systemUnderTest.retryLastMessage()

        systemUnderTest.isLoading shouldBe true
    }

    @Test
    fun `When retrying an Error message completes then loading is set to false`() {
        fakeChatMessageHandler.responseMessage = ChatMessage.Api.Error("irrelevant")
        sendMessageAndAdvance()

        systemUnderTest.retryLastMessage()
        unconfinedTestDispatcher.scheduler.advanceUntilIdle()

        systemUnderTest.isLoading shouldBe false
    }

    @Test
    fun `When retrying an Error message completes then messages are updated user message and response`() {
        fakeChatMessageHandler.responseMessage = ChatMessage.Api.Error("irrelevant")
        sendMessageAndAdvance()

        systemUnderTest.retryLastMessage()
        unconfinedTestDispatcher.scheduler.advanceUntilIdle()

        systemUnderTest.messages shouldHaveSize 4
    }

    @Test
    fun `When retrying a Successful message then nothing happens`() {
        fakeChatMessageHandler.responseMessage = ChatMessage.Api.Success("irrelevant")
        sendMessageAndAdvance()

        systemUnderTest.retryLastMessage()
        unconfinedTestDispatcher.scheduler.advanceUntilIdle()

        systemUnderTest.messages shouldHaveSize 2
    }

    private fun sendMessageAndAdvance() {
        systemUnderTest.sendMessage()
        unconfinedTestDispatcher.scheduler.advanceUntilIdle()
    }
}