import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@Composable
fun App() {
    MaterialTheme {
        var userMessage by remember { mutableStateOf("") }
        val messages =
            remember { mutableStateListOf<String>() }
        Column(
            Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LazyColumn(Modifier.weight(1f)) {
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
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = userMessage,
                    onValueChange = { userMessage = it },
                    modifier = Modifier.weight(1f),
                    maxLines = 10,
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Button(
                        onClick = {
                            messages.add(userMessage)
                            messages.add("Answer: ${userMessage.reversed()}")
                            userMessage = ""
                        }
                    ) {
                        Text("Send")
                    }
                    // TODO count tokens?
                    Text(
                        text = userMessage.count().toString(),
                        style = MaterialTheme.typography.body2.copy(fontSize = 10.sp),
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

expect fun getPlatformName(): String