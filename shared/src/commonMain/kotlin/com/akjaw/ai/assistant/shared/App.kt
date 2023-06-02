package com.akjaw.ai.assistant.shared

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.ktor.client.HttpClient
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import co.touchlab.kermit.Logger.Companion as KermitLogger

@Composable
fun App() {
    MaterialTheme(colors = MaterialTheme.colors.copy(primary = Color(0xFF4E7DF8))) {
        val scope = rememberCoroutineScope()
        Column(modifier = Modifier.padding(horizontal = 8.dp)) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                TypeButton("notion_logo.xml", true)
            }
            Spacer(modifier = Modifier.height(4.dp))
            ChatScreen(remember { ChatScreenStateHolder(scope) })
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun TypeButton(resource: String, isSelected: Boolean) {
    val boredColor = remember(isSelected) { if (isSelected) Color.Black else Color.Gray }
    Box(
        Modifier.border(1.dp, boredColor, RoundedCornerShape(size = 4.dp)).padding(4.dp)
    ) {
        Image(
            painter = painterResource(resource),
            contentDescription = null,
            modifier = Modifier.size(48.dp)
        )
    }
}

val ktorClient = HttpClient {
    install(Logging) {
        level = LogLevel.ALL
        logger = object : Logger {
            override fun log(message: String) {
                KermitLogger.i(tag = "Ktor") { message }
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
        url(Endpoints.AddTask.URL)
        headers {
            set("authorization", Endpoints.AddTask.AUTH)
        }
    }
}

val jsonSerialization = Json {
    prettyPrint = true
    isLenient = true
}

sealed interface ChatMessage {

    val message: String

    data class User(override val message: String) : ChatMessage

    sealed interface Api : ChatMessage {

        data class Success(override val message: String) : Api

        data class Error(override val message: String) : Api
    }
}

class AddTask(
    private val client: HttpClient = ktorClient,
    private val json: Json = jsonSerialization,
) {

    @Serializable
    data class Request(val task: String)

    suspend fun execute(task: String): ChatMessage {
        val response = client.post {
            contentType(ContentType.Application.Json)
            setBody(json.encodeToString(Request(task)))
        }

        return if (response.status == HttpStatusCode.OK) {
            ChatMessage.Api.Success(response.bodyAsText())
        } else {
            ChatMessage.Api.Error(response.bodyAsText())
        }
    }
}

// TODO add rememberSaveable
class ChatScreenStateHolder(
    private val coroutineScope: CoroutineScope,
    private val addTask: AddTask = AddTask()
) {

    var userMessage: String by mutableStateOf("")
        private set
    val count by derivedStateOf {
        // TODO count tokens?
        userMessage.count()
    }

    private val mutableMessages = mutableStateListOf<ChatMessage>()
    val messages: List<ChatMessage> = mutableMessages

    fun updateUserMessage(message: String) {
        userMessage = message
    }

    fun sendMessage() {
        val message = userMessage
        mutableMessages.add(ChatMessage.User(message))
        userMessage = ""
        coroutineScope.launch {
            val response = addTask.execute(message)
            mutableMessages.add(response)
        }
    }
}

@Composable
private fun ChatScreen(stateHolder: ChatScreenStateHolder) {
    // TODO add buttons at the top which changes the "Content"
    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f)
                .fillMaxWidth()
                .border(1.dp, Color.Gray, shape = RoundedCornerShape(size = 4.dp))
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
            items(
                items = stateHolder.messages,
                key = { it.toString() }
            ) {message ->
                val background = when (message) {
                    is ChatMessage.User -> Color.White
                    is ChatMessage.Api.Error -> Color(0xFFFF7878)
                    is ChatMessage.Api.Success -> Color(0xFFC9E6C4)
                }
                Card(modifier = Modifier.fillMaxWidth(), backgroundColor = background) {
                    Text(message.message, modifier = Modifier.fillMaxWidth().padding(8.dp))
                }
            }
        }
        ChatInput(
            userMessage = stateHolder.userMessage,
            setUserMessage = stateHolder::updateUserMessage,
            count = stateHolder.count,
            onSend = stateHolder::sendMessage,
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun ChatInput(
    userMessage: String,
    setUserMessage: (String) -> Unit,
    count: Int,
    onSend: () -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(
            value = userMessage,
            onValueChange = setUserMessage,
            modifier = Modifier.weight(1f),
            maxLines = 10,
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = onSend) {
                Text("Send")
            }
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.body2.copy(fontSize = 10.sp),
            )
        }
    }
}

expect fun getPlatformName(): String