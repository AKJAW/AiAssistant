package com.akjaw.ai.assistant.shared.chat.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.akjaw.ai.assistant.shared.chat.domain.model.ChatMessage
import com.akjaw.ai.assistant.shared.chat.presentation.ChatScreenStateHolder
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

@Composable
fun ChatScreen(stateHolder: ChatScreenStateHolder) {
    val messages = stateHolder.messages

    val lazyListState = rememberLazyListState()
    val isKeyboardOpen by keyboardAsState()
    LaunchedEffect(messages.count(), isKeyboardOpen) {
        delay(200)
        if (messages.isNotEmpty()) lazyListState.animateScrollToItem(messages.lastIndex)
    }

    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        LazyColumn(
            state = lazyListState,
            modifier = Modifier.weight(1f)
                .fillMaxWidth()
                .border(1.dp, Color.Gray, shape = RoundedCornerShape(size = 4.dp))
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            itemsIndexed(
                items = messages,
                key = { index, _ -> index }
            ) { _, message ->
                Message(
                    message,
                    stateHolder::retryLastMessage
                )
            }
        }
        ChatInput(
            userMessage = stateHolder.userMessage,
            setUserMessage = stateHolder::updateUserMessage,
            count = stateHolder.count,
            onSend = stateHolder::sendMessage,
            isEnabled = stateHolder.isLoading.not(),
        )
        Spacer(modifier = Modifier.height(4.dp))
    }
}

@Composable
private fun Message(
    message: ChatMessage,
    onRetry: () -> Unit
) {
    val clipboardManager: ClipboardManager = LocalClipboardManager.current
    val background = when (message) {
        is ChatMessage.User -> Color.White
        is ChatMessage.Api.Error -> Color(0xFFFF7878)
        is ChatMessage.Api.Success -> Color(0xFFC9E6C4)
    }
    SelectionContainer {
        Box {
            Card(modifier = Modifier.fillMaxWidth(), backgroundColor = background) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        message.message,
                        modifier = Modifier.fillMaxWidth().padding(8.dp).weight(1f)
                    )
                    if (message is ChatMessage.Api.Error) {
                        IconButton(
                            onClick = onRetry,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Image(
                                painter = painterResource("refresh.xml"),
                                contentDescription = "refresh",
                                modifier = Modifier.size(48.dp),
                            )
                        }
                        Spacer(Modifier.width(4.dp))
                    }
                }
            }
            Image(
                painter = painterResource("copy.xml"),
                contentDescription = "copy",
                modifier = Modifier.size(32.dp)
                    .padding(top = 4.dp, end = 4.dp)
                    .align(Alignment.TopEnd)
                    .clickable {
                        clipboardManager.setText(AnnotatedString(message.message))
                    },
                alpha = 0.3f,
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun ChatInput(
    userMessage: String,
    setUserMessage: (String) -> Unit,
    count: Int,
    onSend: () -> Unit,
    isEnabled: Boolean,
) {
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(null) {
        delay(50)
        focusRequester.requestFocus()
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        OutlinedTextField(
            value = userMessage,
            onValueChange = setUserMessage,
            modifier = Modifier.weight(1f).focusRequester(focusRequester)
                .onKeyEvent { keyEvent ->
                    if (keyEvent.key == Key.Enter && keyEvent.isShiftPressed) {
                        onSend()
                        true
                    } else {
                        false
                    }
                },
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

@Composable
fun keyboardAsState(): State<Boolean> {
    val isImeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0
    return rememberUpdatedState(isImeVisible)
}
