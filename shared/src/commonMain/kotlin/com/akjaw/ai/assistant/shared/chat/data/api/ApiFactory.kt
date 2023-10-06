package com.akjaw.ai.assistant.shared.chat.data.api

import com.akjaw.ai.assistant.shared.Endpoints
import com.akjaw.ai.assistant.shared.chat.domain.ValueSender
import com.akjaw.ai.assistant.shared.chat.domain.ApiValueSender
import com.akjaw.ai.assistant.shared.chat.domain.FakeValueSender
import io.ktor.client.HttpClient

interface ApiFactory {

    fun createAddTickTickTask(): ValueSender

    fun createAddNotionTask(): ValueSender
}

class ProductionApiFactory(
    private val client: HttpClient,
) : ApiFactory {

    override fun createAddTickTickTask(): ValueSender = ApiValueSender(
        client = client,
        endpointUrl = Endpoints.AddTaskTickTick.URL,
        auth = Endpoints.AddTaskTickTick.AUTH
    )

    override fun createAddNotionTask(): ValueSender = ApiValueSender(
        client = client,
        endpointUrl = Endpoints.AddTaskNotion.URL,
        auth = Endpoints.AddTaskNotion.AUTH
    )
}

class FakeApiFactory : ApiFactory {

    override fun createAddTickTickTask(): ValueSender = FakeValueSender("TickTick")

    override fun createAddNotionTask(): ValueSender = FakeValueSender("Notion")
}
