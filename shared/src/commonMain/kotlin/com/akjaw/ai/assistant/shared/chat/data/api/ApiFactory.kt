package com.akjaw.ai.assistant.shared.chat.data.api

import com.akjaw.ai.assistant.shared.Endpoints
import com.akjaw.ai.assistant.shared.chat.domain.AddTask
import io.ktor.client.HttpClient

interface ApiFactory {

    fun createAddTickTickTask(): AddTask

    fun createAddNotionTask(): AddTask
}

class ProductionApiFactory(
    private val client: HttpClient,
) : ApiFactory {

    override fun createAddTickTickTask() = AddTask(
        client = client,
        endpointUrl = Endpoints.AddTaskTickTick.URL,
        auth = Endpoints.AddTaskTickTick.AUTH
    )

    override fun createAddNotionTask() = AddTask(
        client = client,
        endpointUrl = Endpoints.AddTaskNotion.URL,
        auth = Endpoints.AddTaskNotion.AUTH
    )
}
