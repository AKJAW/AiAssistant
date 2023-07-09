package com.akjaw.ai.assistant.shared.chat.domain

import com.akjaw.ai.assistant.shared.Endpoints
import com.akjaw.ai.assistant.shared.chat.domain.model.ChatMessage
import com.akjaw.ai.assistant.shared.dashboard.domain.ChatType
import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json

// TODO remove later
internal fun createAddTickTickTask(client: HttpClient) = AddTask(
    client = client,
    endpointUrl = Endpoints.AddTaskTickTick.URL,
    auth = Endpoints.AddTaskTickTick.AUTH
)

// TODO remove later
internal fun createAddNotionTask(client: HttpClient) = AddTask(
    client = client,
    endpointUrl = Endpoints.AddTaskNotion.URL,
    auth = Endpoints.AddTaskNotion.AUTH
)

internal class ChatMessageHandler(
    client: HttpClient = createKtorClient(),
) {
    private val addNotionTask = createAddNotionTask(client)
    private val addTickTickTask = createAddTickTickTask(client)

    suspend fun sendMessage(text: String, type: ChatType): ChatMessage {
        return when (type) {
            ChatType.Notion -> addNotionTask.execute(text)
            ChatType.TickTick -> addTickTickTask.execute(text)
        }
    }
}
