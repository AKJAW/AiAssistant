package com.akjaw.ai.assistant.shared.chat.domain

import app.cash.turbine.test
import com.akjaw.ai.assistant.database.Database
import com.akjaw.ai.assistant.shared.chat.data.database.InMemoryDriverFactory
import com.akjaw.ai.assistant.shared.chat.data.database.createDatabase
import com.akjaw.ai.assistant.shared.chat.domain.model.ChatMessage
import com.akjaw.ai.assistant.shared.chat.helpers.createChatMessageHandler
import com.akjaw.ai.assistant.shared.dashboard.domain.ChatType
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class ChatMessageHandlerCommonTest {

    private lateinit var database: Database
    private lateinit var systemUnderTest: ChatMessageHandler

    @BeforeTest
    fun setUp() {
        database = createDatabase(InMemoryDriverFactory())
        systemUnderTest = createChatMessageHandler(database = database)
    }

    @Test
    fun `When Database has data then it is returned`() = runTest {
        database.messageEntityQueries.insert(null, "1", 1, ChatType.Notion, true)
        database.messageEntityQueries.insert(null, "2", 2, ChatType.Notion, false)

        val result = systemUnderTest.getMessagesForType(ChatType.Notion)

        result.test {
            val result = awaitItem()
            result[0] shouldBe ChatMessage.User("1")
            result[1] shouldBe ChatMessage.Api.Success("2")
        }
    }
}
