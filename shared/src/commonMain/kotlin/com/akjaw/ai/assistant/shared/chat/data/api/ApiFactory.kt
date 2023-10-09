package com.akjaw.ai.assistant.shared.chat.data.api

import com.akjaw.ai.assistant.shared.Endpoints
import com.akjaw.ai.assistant.shared.chat.domain.AddStoryRequest
import com.akjaw.ai.assistant.shared.chat.domain.AddTaskRequest
import com.akjaw.ai.assistant.shared.chat.domain.ValueSender
import com.akjaw.ai.assistant.shared.chat.domain.ApiValueSender
import com.akjaw.ai.assistant.shared.chat.domain.FakeValueSender
import com.akjaw.ai.assistant.shared.chat.domain.NewApiValueSender
import com.akjaw.ai.assistant.shared.composition.Dependencies
import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString

interface ApiFactory {

    fun createAddTickTickTask(): ValueSender<AddTaskRequest>

    fun createAddNotionTask(): ValueSender<String>

    fun createAddNotionStory(): ValueSender<String>
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

    override fun createAddNotionTask(): ValueSender<String> = NewApiValueSender(
        client = client,
        endpointUrl = Endpoints.GenericNotion.URL,
        auth = Endpoints.GenericNotion.AUTH,
        type = Endpoints.GenericNotion.Type.Task.name,
    )

    override fun createAddNotionStory(): ValueSender<String> = NewApiValueSender(
        client = client,
        endpointUrl = Endpoints.GenericNotion.URL,
        auth = Endpoints.GenericNotion.AUTH,
        type = Endpoints.GenericNotion.Type.Story.name,
    )
}

class FakeApiFactory : ApiFactory {

    override fun createAddTickTickTask(): ValueSender<AddTaskRequest> = FakeValueSender("TickTickTask")

    override fun createAddNotionTask(): ValueSender<String> = FakeValueSender("NotionTask")

    override fun createAddNotionStory(): ValueSender<String> = FakeValueSender("NotionStory")
}
