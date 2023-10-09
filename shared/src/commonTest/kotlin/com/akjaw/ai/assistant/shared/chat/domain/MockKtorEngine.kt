package com.akjaw.ai.assistant.shared.chat.domain

import com.akjaw.ai.assistant.shared.Endpoints
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.content.TextContent
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.OutgoingContent
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

class MockKtorEngine {

    sealed class Result {
        abstract val message: String

        data class Success(override val message: String) : Result()
        data class Failure(override val message: String) : Result()
    }

    private val jsonSerialization = Json {
        prettyPrint = true
        isLenient = true
    }
    var apiResult: Result = Result.Success("")
    var passedInTask: String? = null

    val engine = MockEngine { request ->
        when (request.url.toString()) {
            Endpoints.GenericNotion.URL -> handleGenericRequest(request)
            Endpoints.AddTaskTickTick.URL -> handleAddTaskRequest(request)
            else -> throw IllegalStateException("URL not supported")
        }
    }

    private fun MockRequestHandleScope.handleAddTaskRequest(
        request: HttpRequestData
    ): HttpResponseData {
        val parsed = request.body.getJson()
        passedInTask = parsed.jsonObject["task"]!!.jsonPrimitive.content

        return respondBasedOnResult()
    }

    private fun MockRequestHandleScope.handleGenericRequest(
        request: HttpRequestData
    ): HttpResponseData {
        val parsed = request.body.getJson()
        passedInTask = parsed.jsonObject["data"]!!.jsonObject["value"]!!.jsonPrimitive.content

        return respondBasedOnResult()
    }

    private fun OutgoingContent.getJson(): JsonElement {
        val input = (this as TextContent).text
        return jsonSerialization.parseToJsonElement(input)
    }

    private fun MockRequestHandleScope.respondBasedOnResult() =
        when (apiResult) {
            is Result.Success -> respond(apiResult.message)
            is Result.Failure -> respond(apiResult.message, HttpStatusCode.BadRequest)
        }
}
