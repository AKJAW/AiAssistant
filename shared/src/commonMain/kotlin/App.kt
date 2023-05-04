import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalResourceApi::class)
@Composable
fun App() {
    MaterialTheme {
        var userMessage by remember { mutableStateOf("") }
        val messages =
            remember { mutableStateListOf<String>("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nunc et eros at nulla laoreet mollis. Donec a viverra turpis. Ut porta at turpis sit amet molestie. Morbi dignissim hendrerit lectus, ac cursus ante dapibus eu. Quisque lobortis metus quam, quis semper sapien interdum eget. Ut aliquam orci eget mauris tristique porttitor. Aliquam sit amet diam quis diam vestibulum egestas. Praesent rutrum erat at hendrerit euismod. Aenean volutpat sed urna bibendum molestie. Sed fringilla lacus viverra, viverra justo iaculis, dictum libero. Donec quis urna euismod, porttitor nulla vel, lobortis erat. Mauris ac nulla enim. Donec ultricies pharetra placerat. Pellentesque convallis luctus tristique.") }
        Column(
            Modifier.fillMaxWidth().padding(horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))
            messages.forEach { message ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Text(message, modifier = Modifier.fillMaxWidth().padding(8.dp))
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = userMessage,
                    onValueChange = { userMessage = it },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Button(
                    onClick = {
                        messages.add(userMessage)
                        userMessage = ""
                    }
                ) {
                    Text("Send")
                }
            }
        }
    }
}

expect fun getPlatformName(): String