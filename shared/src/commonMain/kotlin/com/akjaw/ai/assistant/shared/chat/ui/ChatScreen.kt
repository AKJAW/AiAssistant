package com.akjaw.ai.assistant.shared.chat.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.akjaw.ai.assistant.shared.chat.domain.model.ChatMessage
import com.akjaw.ai.assistant.shared.chat.presentation.ChatScreenStateHolder
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@ExperimentalResourceApi
@Composable
fun ChatScreen(stateHolder: ChatScreenStateHolder) {
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
            itemsIndexed(
                items = stateHolder.messages,
                key = { index, _ -> index }
            ) { _, message ->
                val background = when (message) {
                    is ChatMessage.User -> Color.White
                    is ChatMessage.Api.Error -> Color(0xFFFF7878)
                    is ChatMessage.Api.Success -> Color(0xFFC9E6C4)
                }
                Card(modifier = Modifier.fillMaxWidth(), backgroundColor = background) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            message.message,
                            modifier = Modifier.fillMaxWidth().padding(8.dp).weight(1f)
                        )
                        if (message is ChatMessage.Api.Error) {
                            IconButton(
                                onClick = stateHolder::retryLastMessage,
                                modifier = Modifier.size(24.dp)
                            ) {
                                Image(
                                    painter = painterResource("refresh.xml"),
                                    contentDescription = "refresh",
                                    modifier = Modifier.size(48.dp)
                                )
                            }
                            Spacer(Modifier.width(4.dp))
                        }
                    }
                }
            }
        }
        ChatInput(
            userMessage = stateHolder.userMessage,
            setUserMessage = stateHolder::updateUserMessage,
            count = stateHolder.count,
            onSend = stateHolder::sendMessage,
            isEnabled = stateHolder.isLoading.not(),
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
    isEnabled: Boolean,
) {
    val focusManager = LocalFocusManager.current
    Row(verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(
            value = userMessage,
            onValueChange = setUserMessage,
            modifier = Modifier.weight(1f),
            maxLines = 10,
            enabled = isEnabled
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Button(
                onClick = {
                    focusManager.clearFocus()
                    onSend()
                },
                enabled = isEnabled
            ) {
                Text("Send")
            }
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.body2.copy(fontSize = 10.sp),
            )
        }
    }
}
