package com.akjaw.ai.assistant.shared.chat.data.api

import com.akjaw.ai.assistant.shared.Endpoints
import com.akjaw.ai.assistant.shared.chat.domain.AddStoryRequest
import com.akjaw.ai.assistant.shared.chat.domain.AddTaskRequest
import com.akjaw.ai.assistant.shared.chat.domain.ValueSender
import com.akjaw.ai.assistant.shared.chat.domain.ApiValueSender
import com.akjaw.ai.assistant.shared.chat.domain.FakeValueSender
import com.akjaw.ai.assistant.shared.composition.Dependencies
import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

interface ApiFactory {

    fun createAddTickTickTask(): ValueSender<AddTaskRequest>

    fun createAddNotionTask(): ValueSender<AddTaskRequest>

    fun createAddNotionStory(): ValueSender<AddStoryRequest>
}


class ProductionApiFactory(
    private val client: HttpClient,
) : ApiFactory {

    private val json: Json = Dependencies.jsonSerialization

    override fun createAddTickTickTask(): ValueSender<AddTaskRequest> = ApiValueSender(
        client = client,
        endpointUrl = Endpoints.AddTaskTickTick.URL,
        auth = Endpoints.AddTaskTickTick.AUTH,
        parseToJson = { json.encodeToString(it) },
    )

    override fun createAddNotionTask(): ValueSender<AddTaskRequest> = ApiValueSender(
        client = client,
        endpointUrl = Endpoints.AddTaskNotion.URL,
        auth = Endpoints.AddTaskNotion.AUTH,
        parseToJson = { json.encodeToString(it) },
    )

    override fun createAddNotionStory(): ValueSender<AddStoryRequest> = ApiValueSender(
        client = client,
        endpointUrl = Endpoints.AddStoryNotion.URL,
        auth = Endpoints.AddStoryNotion.AUTH,
        parseToJson = { json.encodeToString(it) },
    )
}

class FakeApiFactory : ApiFactory {

    override fun createAddTickTickTask(): ValueSender<AddTaskRequest> = FakeValueSender("TickTickTask")

    override fun createAddNotionTask(): ValueSender<AddTaskRequest> = FakeValueSender("NotionTask")

    override fun createAddNotionStory(): ValueSender<AddStoryRequest> = FakeValueSender("NotionStory")
}
