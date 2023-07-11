package com.akjaw.ai.assistant.shared.chat.domain

import com.akjaw.ai.assistant.shared.chat.domain.model.ChatMessage
import com.akjaw.ai.assistant.shared.composition.Dependencies
import io.ktor.client.HttpClient
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

// TODO make more generic, so it can be used for any type of message
interface AddTask {

    suspend fun execute(task: String): ChatMessage
}

class AddTaskInApi(
    private val client: HttpClient,
    private val endpointUrl: String,
    private val auth: String,
) : AddTask {

    @Serializable
    private data class Request(val task: String)

    private val json: Json = Dependencies.jsonSerialization

    override suspend fun execute(task: String): ChatMessage {
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

class FakeAddTask(private val name: String) : AddTask {

    override suspend fun execute(task: String): ChatMessage {
        return ChatMessage.Api.Success("$name added task: $task")
    }
}
