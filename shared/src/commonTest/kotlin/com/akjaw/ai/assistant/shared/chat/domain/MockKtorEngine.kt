package com.akjaw.ai.assistant.shared.chat.domain

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.content.TextContent
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.json.Json
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
    var apiResult : Result = Result.Success("")
    var passedInTask: String? = null

    val engine = MockEngine { request ->
        val input = (request.body as TextContent).text
        val parsed = jsonSerialization.parseToJsonElement(input)
        passedInTask = parsed.jsonObject["task"]!!.jsonPrimitive.content
        when (apiResult) {
            is Result.Success -> respond(apiResult.message)
            is Result.Failure -> respond(apiResult.message, HttpStatusCode.BadRequest)
        }
    }
}
