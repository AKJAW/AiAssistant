package com.akjaw.ai.assistant.shared

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ChatCompletion
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.OpenAIConfig
import io.ktor.client.HttpClient
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
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import kotlin.time.Duration.Companion.minutes
import co.touchlab.kermit.Logger.Companion as KermitLogger

val openAI = OpenAI(
    OpenAIConfig(
        token = OpenAiSecret.apiKey,
        timeout = Timeout(request = 2.minutes, connect = 2.minutes, socket = 2.minutes)
    )
)

@Composable
fun App() {
    MaterialTheme {
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
        logger = object: Logger {
            override fun log(message: String) {
                KermitLogger.i(tag = "Ktor") { message }
            }
        }
    }
    install(ContentNegotiation) {
        json()
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

class AddTask(
    private val client: HttpClient = ktorClient,
    private val json: Json = jsonSerialization,
) {

    @Serializable
    data class Request(val task: String)

    suspend fun execute(task: String): String {
        val response = client.post {
            contentType(ContentType.Application.Json)
            setBody(json.encodeToString(Request(task)))
        }
        return response.bodyAsText()
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

    private val mutableMessages = mutableStateListOf<String>()
    val messages: List<String> = mutableMessages

    fun updateUserMessage(message: String) {
        userMessage = message
    }

    fun sendMessage() {
        val message = userMessage
        mutableMessages.add(message)
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
            itemsIndexed(items = stateHolder.messages, key = { index, _ -> index }) { index, message ->
                val background = if (index % 2 == 1) Color(0xFFC9E6C4) else Color.White
                Card(modifier = Modifier.fillMaxWidth(), backgroundColor = background) {
                    Text(message, modifier = Modifier.fillMaxWidth().padding(8.dp))
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

@OptIn(BetaOpenAI::class)
private suspend fun askChatGpt(message: String): String? {
    val chatCompletionRequest = ChatCompletionRequest(
        model = ModelId("gpt-3.5-turbo"),
        messages = listOf(
            ChatMessage(
                role = ChatRole.User,
                content = message
            )
        )
    )
    val completion: ChatCompletion = openAI.chatCompletion(chatCompletionRequest)
    return completion.choices.first().message?.content
}

expect fun getPlatformName(): String