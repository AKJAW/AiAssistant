package com.akjaw.ai.assistant.shared.chat.domain

import com.akjaw.ai.assistant.shared.chat.domain.model.ChatMessage
import io.ktor.client.HttpClient
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.Serializable

// TODO make more generic, so it can be used for any type of message
interface ValueSender<Request> {

    suspend fun execute(request: Request): ChatMessage
}

@Serializable
data class AddTaskRequest(val task: String)

@Serializable
data class AddStoryRequest(val story: String)

class ApiValueSender<Request>(
    private val client: HttpClient,
    private val endpointUrl: String,
    private val auth: String,
    private val parseToJson: (Request) -> String,
) : ValueSender<Request> {

    override suspend fun execute(request: Request): ChatMessage {
        val response = client.post(endpointUrl) {
            headers {
                set("authorization", auth)
            }
            setBody(parseToJson(request))
        }

        return if (response.status == HttpStatusCode.OK) {
            ChatMessage.Api.Success(response.bodyAsText())
        } else {
            ChatMessage.Api.Error(response.bodyAsText())
        }
    }
}

@Serializable
data class NewRequest(
    val auth: String,
    val type: String,
    val data: Data,
) {
    @Serializable
    data class Data(val value: String)
}

class NewApiValueSender(
    private val client: HttpClient,
    private val endpointUrl: String,
    private val auth: String,
    private val type: String,
) : ValueSender<String> {

    override suspend fun execute(value: String): ChatMessage {
        val response = client.post(endpointUrl) {
            setBody(
                NewRequest(
                    auth = auth,
                    type = type,
                    data = NewRequest.Data(value = value)
                )
            )
        }

        return if (response.status == HttpStatusCode.OK) {
            ChatMessage.Api.Success(response.bodyAsText())
        } else {
            ChatMessage.Api.Error(response.bodyAsText())
        }
    }
}

class FakeValueSender<Request>(private val name: String) : ValueSender<Request> {

    override suspend fun execute(request: Request): ChatMessage {
        return ChatMessage.Api.Success("$name added task: $request")
    }
}
