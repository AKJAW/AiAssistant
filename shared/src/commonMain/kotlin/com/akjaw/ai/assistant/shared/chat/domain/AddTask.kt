package com.akjaw.ai.assistant.shared.chat.domain

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

internal val jsonSerialization = Json {
    prettyPrint = true
    isLenient = true
}

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

class AddTask(
    private val client: HttpClient,
    private val endpointUrl: String,
    private val auth: String,
) {

    @Serializable
    data class Request(val task: String)

    private val json: Json = jsonSerialization

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
        ChatType.Notion -> createAddNotionTask(createKtorClient())
        ChatType.TickTick -> createAddTickTickTask(createKtorClient())
    }
}
