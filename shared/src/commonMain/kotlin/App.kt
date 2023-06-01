import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import kotlin.time.Duration.Companion.minutes

val openAI = OpenAI(
    OpenAIConfig(
        token = OpenAiSecret.apiKey,
        timeout = Timeout(request = 2.minutes, connect = 2.minutes, socket = 2.minutes)
    )
)

@Composable
fun App() {
    MaterialTheme {
        ChatScreen()
    }
}

@Composable
private fun ChatScreen() {
    var userMessage by remember { mutableStateOf("") }
    val messages =
        remember { mutableStateListOf<String>() }
    // TODO extract screen
    // TODO extract input?
    // TODO add buttons at the top which changes the "Content"
    Column(
        Modifier.fillMaxWidth().padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
            itemsIndexed(items = messages, key = { index, _ -> index }) { index, message ->
                val background = if (index % 2 == 1) Color.LightGray else Color.White
                Card(modifier = Modifier.fillMaxWidth(), backgroundColor = background) {
                    Text(message, modifier = Modifier.fillMaxWidth().padding(8.dp))
                }
            }
        }
        ChatInput(
            userMessage = userMessage,
            setUserMessage = { userMessage = it },
            onSend = { },
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun ChatInput(
    userMessage: String,
    setUserMessage: (String) -> Unit,
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
            // TODO count tokens?
            Text(
                text = userMessage.count().toString(),
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