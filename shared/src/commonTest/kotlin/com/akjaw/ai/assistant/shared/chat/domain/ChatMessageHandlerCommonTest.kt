package com.akjaw.ai.assistant.shared.chat.domain

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.turbine.test
import com.akjaw.ai.assistant.database.Database
import com.akjaw.ai.assistant.database.MessageEntity
import com.akjaw.ai.assistant.shared.chat.data.database.InMemoryDriverFactory
import com.akjaw.ai.assistant.shared.chat.data.database.createDatabase
import com.akjaw.ai.assistant.shared.chat.domain.model.ChatMessage
import com.akjaw.ai.assistant.shared.chat.helpers.StubTimestampProvider
import com.akjaw.ai.assistant.shared.chat.helpers.createPersistedApiChatMessageHandler
import com.akjaw.ai.assistant.shared.dashboard.domain.ChatType
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class ChatMessageHandlerCommonTest {

    private lateinit var stubTimestampProvider: StubTimestampProvider
    private lateinit var database: Database
    private lateinit var mockKtorEngine: MockKtorEngine
    private lateinit var systemUnderTest: ChatMessageHandler

    @BeforeTest
    fun setUp() {
        database = createDatabase(InMemoryDriverFactory())
        stubTimestampProvider = StubTimestampProvider()
        mockKtorEngine = MockKtorEngine()
        systemUnderTest = createPersistedApiChatMessageHandler(
            database = database,
            timestampProvider = stubTimestampProvider,
            engine = mockKtorEngine.engine,
        )
    }

    @Test
    fun `When Database has data then it is returned`() = runTest {
        database.messageEntityQueries.insert(null, "1", 1, ChatType.Notion, true)
        database.messageEntityQueries.insert(null, "2", 2, ChatType.Notion, false)

        val result = systemUnderTest.getMessagesForType(ChatType.Notion)

        result.test {
            assertSoftly(awaitItem()) {
                shouldHaveSize(2)
                get(0) shouldBe ChatMessage.User("1")
                get(1) shouldBe ChatMessage.Api.Success("2")
            }
        }
    }

    @Test
    fun `When message is successfully sent then both users and response is saved`() = runTest {
        mockKtorEngine.apiResult = MockKtorEngine.Result.Success("response")
        stubTimestampProvider.value = 200
        systemUnderTest.sendMessage("message", ChatType.Notion)

        getDatabaseMessages(ChatType.Notion).test {
            assertSoftly(awaitItem()) {
                shouldHaveSize(2)
                get(0) shouldBe MessageEntity(1, "message", 200, ChatType.Notion, true)
                get(1) shouldBe MessageEntity(2, "response", 200, ChatType.Notion, false)
            }
        }
    }

    @Test
    fun `When message has send error then no messages are saved`() = runTest {
        mockKtorEngine.apiResult = MockKtorEngine.Result.Failure("irrelevant")
        systemUnderTest.sendMessage("message", ChatType.Notion)

        getDatabaseMessages(ChatType.Notion).test {
            assertSoftly(awaitItem()) {
                shouldHaveSize(0)
            }
        }
    }

    private fun getDatabaseMessages(chatType: ChatType) =
        database.messageEntityQueries.selectByType(chatType)
            .asFlow().mapToList(UnconfinedTestDispatcher())
}
