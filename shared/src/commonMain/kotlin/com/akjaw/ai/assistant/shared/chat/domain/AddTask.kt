package com.akjaw.ai.assistant.shared.chat.domain

import com.akjaw.ai.assistant.shared.Endpoints
import com.akjaw.ai.assistant.shared.chat.domain.model.ChatMessage
import com.akjaw.ai.assistant.shared.dashboard.domain.ChatType
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngine
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

// TODO add SendChatMessage UseCase which will take in the type, and manage w/e happens text.

internal fun createKtorClient(engine: HttpClientEngine? = null): HttpClient {
    val config: HttpClientConfig<*>.() -> Unit = {
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
            contentType(ContentType.Application.Json)
        }
    }
    return if (engine == null) {
        HttpClient(config)
    } else {
        HttpClient(engine, config)
    }
}

private val jsonSerialization = Json {
    prettyPrint = true
    isLenient = true
}

abstract class AddTask(
    private val client: HttpClient = createKtorClient(),
    private val json: Json = jsonSerialization,
) {

    @Serializable
    data class Request(val task: String)

    abstract val endpointUrl: String
    abstract val auth: String

    suspend fun execute(task: String): ChatMessage {
        val response = client.post(endpointUrl) {
            headers {
                set("authorization", auth)
            }
            setBody(json.encodeToString(Request(task)))
        }

        return if (response.status == HttpStatusCode.OK) {
            ChatMessage.Api.Success(response.bodyAsText())
        } else {
            ChatMessage.Api.Error(response.bodyAsText())
        }
    }
}

class AddTaskFactory {

    fun create(chatType: ChatType) = when (chatType) {
        ChatType.Notion -> AddNotionTask()
        ChatType.TickTick -> AddTickTickTask()
    }
}

class AddNotionTask(
    client: HttpClient = createKtorClient(),
    json: Json = jsonSerialization,
) : AddTask(client, json) {

    override val endpointUrl: String = Endpoints.AddTaskNotion.URL
    override val auth: String = Endpoints.AddTaskNotion.AUTH
}

class AddTickTickTask(
    client: HttpClient = createKtorClient(),
    json: Json = jsonSerialization,
) : AddTask(client, json) {

    override val endpointUrl: String = Endpoints.AddTaskTickTick.URL
    override val auth: String = Endpoints.AddTaskTickTick.AUTH
}
