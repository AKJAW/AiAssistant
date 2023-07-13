package com.akjaw.ai.assistant.shared.chat.helpers

import com.akjaw.ai.assistant.shared.chat.domain.ChatMessageHandler
import com.akjaw.ai.assistant.shared.chat.domain.model.ChatMessage
import com.akjaw.ai.assistant.shared.dashboard.domain.ChatType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeChatMessageHandler : ChatMessageHandler {

    private val savedMessages: MutableMap<ChatType, List<ChatMessage>> = mutableMapOf()
    var responseMessage: ChatMessage = ChatMessage.Api.Success("")

    fun setSavedMessages(type: ChatType, messages: List<ChatMessage>) {
        savedMessages[type] = messages
    }

    override fun getMessagesForType(chatType: ChatType): Flow<List<ChatMessage>> =
        flow { emit(savedMessages[chatType] ?: listOf()) }

    override suspend fun sendMessage(text: String, type: ChatType): ChatMessage {
        delay(1000)
        return responseMessage
    }
}
