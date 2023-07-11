package com.akjaw.ai.assistant.shared.chat.domain

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.akjaw.ai.assistant.database.Database
import com.akjaw.ai.assistant.shared.Endpoints
import com.akjaw.ai.assistant.shared.chat.data.api.ApiFactory
import com.akjaw.ai.assistant.shared.chat.domain.model.ChatMessage
import com.akjaw.ai.assistant.shared.dashboard.domain.ChatType
import io.ktor.client.HttpClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ChatMessageHandler(
    apiFactory: ApiFactory,
    private val database: Database
) {
    private val addNotionTask = apiFactory.createAddNotionTask()
    private val addTickTickTask = apiFactory.createAddTickTickTask()

    fun getMessagesForType(chatType: ChatType): Flow<List<ChatMessage>> =
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

    suspend fun sendMessage(text: String, type: ChatType): ChatMessage {
        // TODO on success save user message and response

        return when (type) {
            ChatType.Notion -> addNotionTask.execute(text)
            ChatType.TickTick -> addTickTickTask.execute(text)
        }
    }
}
