package com.akjaw.ai.assistant.shared.chat.domain.model

sealed interface ChatMessage {

    val message: String

    data class User(override val message: String) : ChatMessage

    sealed interface Api : ChatMessage {

        data class Success(override val message: String) : Api

        data class Error(override val message: String) : Api
    }
}