package com.akjaw.ai.assistant.shared.chat.presentation

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.akjaw.ai.assistant.shared.chat.domain.AddTask
import com.akjaw.ai.assistant.shared.chat.domain.ChatMessageHandler
import com.akjaw.ai.assistant.shared.chat.domain.model.ChatMessage
import com.akjaw.ai.assistant.shared.dashboard.domain.ChatType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ChatScreenStateHolder(
    val type: ChatType,
    private val coroutineScope: CoroutineScope,
    private val chatMessageHandler: ChatMessageHandler,
) {

    var userMessage: String by mutableStateOf("")
        private set
    val count by derivedStateOf {
        // TODO count tokens?
        userMessage.count()
    }
    var isLoading: Boolean by mutableStateOf(false)
        private set

    private val mutableMessages = mutableStateListOf<ChatMessage>()
    val messages: List<ChatMessage> = mutableMessages

    init {
        coroutineScope.launch {
            val savedMessages = chatMessageHandler.getMessagesForType(type).first()
            mutableMessages.addAll(savedMessages)
        }
    }

    fun updateUserMessage(message: String) {
        userMessage = message
    }

    fun sendMessage() {
        val message = userMessage
        mutableMessages.add(ChatMessage.User(message))
        userMessage = ""
        sendMessageToHandler(message)
    }

    fun retryLastMessage() {
        val secondToLastMessage =
            mutableMessages.getOrNull(mutableMessages.lastIndex - 1)
        val isLastMessageAnError = mutableMessages.lastOrNull() is ChatMessage.Api.Error
        if (secondToLastMessage is ChatMessage.User && isLastMessageAnError) {
            mutableMessages.add(secondToLastMessage)
            sendMessageToHandler(secondToLastMessage.message)
        }
    }

    private fun sendMessageToHandler(message: String) {
        isLoading = true
        coroutineScope.launch {
            val response = chatMessageHandler.sendMessage(message, type)
            mutableMessages.add(response)
            isLoading = false
        }
    }
}