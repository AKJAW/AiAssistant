package com.akjaw.ai.assistant.shared.chat.composition

import com.akjaw.ai.assistant.database.Database
import com.akjaw.ai.assistant.shared.chat.domain.ChatMessageHandler
import com.akjaw.ai.assistant.shared.chat.domain.createKtorClient

object Dependencies {

    internal lateinit var database: Database

    internal val chatMessageHandler: ChatMessageHandler by lazy {
        ChatMessageHandler(createKtorClient(), database)
    }
}
