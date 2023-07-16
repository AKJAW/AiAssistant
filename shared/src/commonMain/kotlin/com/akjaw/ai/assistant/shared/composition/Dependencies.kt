package com.akjaw.ai.assistant.shared.composition

import com.akjaw.ai.assistant.database.Database
import com.akjaw.ai.assistant.shared.chat.data.api.ApiFactory
import com.akjaw.ai.assistant.shared.chat.data.api.FakeApiFactory
import com.akjaw.ai.assistant.shared.chat.data.api.ProductionApiFactory
import com.akjaw.ai.assistant.shared.chat.data.database.DriverFactory
import com.akjaw.ai.assistant.shared.chat.data.database.ProductionDriverFactory
import com.akjaw.ai.assistant.shared.chat.data.database.createDatabase
import com.akjaw.ai.assistant.shared.chat.data.time.KotlinXTimestampProvider
import com.akjaw.ai.assistant.shared.chat.data.time.TimestampProvider
import com.akjaw.ai.assistant.shared.chat.domain.ChatMessageHandler
import com.akjaw.ai.assistant.shared.chat.domain.PersistedApiChatMessageHandler
import com.akjaw.ai.assistant.shared.utils.BuildInfo
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

fun initializeDependencies(
    isDebug: Boolean,
    driverFactory: DriverFactory = ProductionDriverFactory()
) {
    BuildInfo.isDebug = isDebug
    createDatabase(driverFactory)
}

object Dependencies {

    internal lateinit var database: Database

    internal val jsonSerialization = Json {
        prettyPrint = true
        isLenient = true
    }

    private val apiFactory: ApiFactory by lazy {
        if (BuildInfo.isDebug) {
            FakeApiFactory()
        } else {
            ProductionApiFactory(createKtorClient())
        }
    }

    internal val chatMessageHandler: ChatMessageHandler by lazy {
        PersistedApiChatMessageHandler(apiFactory, database, createTimestampProvider())
    }

    internal fun createTimestampProvider(): TimestampProvider = KotlinXTimestampProvider()

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
}
