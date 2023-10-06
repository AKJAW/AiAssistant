package com.akjaw.ai.assistant.shared.chat.domain

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.akjaw.ai.assistant.database.Database
import com.akjaw.ai.assistant.shared.chat.data.api.ApiFactory
import com.akjaw.ai.assistant.shared.chat.data.time.TimestampProvider
import com.akjaw.ai.assistant.shared.chat.domain.model.ChatMessage
import com.akjaw.ai.assistant.shared.dashboard.domain.ChatType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface ChatMessageHandler {

    fun getMessagesForType(chatType: ChatType): Flow<List<ChatMessage>>

    suspend fun sendMessage(text: String, type: ChatType): ChatMessage
}

class PersistedApiChatMessageHandler(
    apiFactory: ApiFactory,
    private val database: Database,
    private val timestampProvider: TimestampProvider,
) : ChatMessageHandler {
    private val addNotionTask = apiFactory.createAddNotionTask()
    private val addTickTickTask = apiFactory.createAddTickTickTask()
    private val addNotionStory = apiFactory.createAddNotionStory()

    override fun getMessagesForType(chatType: ChatType): Flow<List<ChatMessage>> =
        database.messageEntityQueries.selectByType(chatType)
            .asFlow()
            .mapToList(Dispatchers.Default)
            .map { entities ->
                entities.map { entity ->
                    if (entity.isUser) {
                        ChatMessage.User(entity.text)
                    } else {
                        ChatMessage.Api.Success(entity.text)
                    }
                }
            }

    override suspend fun sendMessage(text: String, type: ChatType): ChatMessage {
        val userMessageTimestamp = timestampProvider.getMilliseconds()
        val response: ChatMessage = when (type) {
            ChatType.Notion -> addNotionTask.execute(AddTaskRequest(text))
            ChatType.TickTick -> addTickTickTask.execute(AddTaskRequest(text))
            ChatType.Story -> addNotionStory.execute(AddStoryRequest(text))
        }
        if (response is ChatMessage.Api.Success) {
            database.transaction {
                database.messageEntityQueries.insert(
                    id = null,
                    text = text,
                    timestampMilliseconds = userMessageTimestamp.value,
                    chatType = type,
                    isUser = true
                )
                database.messageEntityQueries.insert(
                    id = null,
                    text = response.message,
                    timestampMilliseconds = userMessageTimestamp.value,
                    chatType = type,
                    isUser = false
                )
            }
        }
        return response
    }
}
