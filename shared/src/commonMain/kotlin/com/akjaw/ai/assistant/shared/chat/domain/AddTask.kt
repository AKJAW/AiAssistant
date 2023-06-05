package com.akjaw.ai.assistant.shared.chat.domain

import com.akjaw.ai.assistant.shared.Endpoints
import com.akjaw.ai.assistant.shared.chat.domain.model.ChatMessage
import com.akjaw.ai.assistant.shared.dashboard.domain.ChatType
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


private val ktorClient = HttpClient {
    install(Logging) {
        level = LogLevel.ALL
        logger = object : Logger {
            override fun log(message: String) {
                co.touchlab.kermit.Logger.i(tag = "Ktor") { message }
            }
        }
    }
    install(ContentNegotiation) {
        json()
    }
    install(HttpTimeout) {
        requestTimeoutMillis = 30_000
        connectTimeoutMillis = 30_000
        socketTimeoutMillis = 30_000
    }
    defaultRequest {
        url(Endpoints.AddTaskNotion.URL)
        headers {
            set("authorization", Endpoints.AddTaskNotion.AUTH)
        }
    }
}

private val jsonSerialization = Json {
    prettyPrint = true
    isLenient = true
}

interface AddTask {

    @Serializable
    data class Request(val task: String)

    suspend fun execute(task: String): ChatMessage
}

class AddTaskFactory {

    fun create(chatType: ChatType) = when (chatType) {
        ChatType.Notion -> FakeAddNotionTask()
        ChatType.TickTick -> FakeAddTickTickTask()
    }
}

class FakeAddNotionTask : AddTask {

    override suspend fun execute(task: String): ChatMessage {
        return ChatMessage.Api.Success("Notion $task")
    }
}

class FakeAddTickTickTask : AddTask {

    override suspend fun execute(task: String): ChatMessage {
        return ChatMessage.Api.Success("TickTick $task")
    }
}

class AddNotionTask(
    private val client: HttpClient = ktorClient,
    private val json: Json = jsonSerialization,
) : AddTask {

    override suspend fun execute(task: String): ChatMessage {
        val response = client.post {
            contentType(ContentType.Application.Json)
            setBody(json.encodeToString(AddTask.Request(task)))
        }

        return if (response.status == HttpStatusCode.OK) {
            ChatMessage.Api.Success(response.bodyAsText())
        } else {
            ChatMessage.Api.Error(response.bodyAsText())
        }
    }
}