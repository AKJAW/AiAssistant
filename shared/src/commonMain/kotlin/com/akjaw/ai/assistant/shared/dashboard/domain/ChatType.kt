package com.akjaw.ai.assistant.shared.dashboard.domain

enum class ChatType(val selectedResource: String, val unselectedResource: String) {
    Notion("notion_logo.xml", "notion_logo_unselected.xml"),
    TickTick("ticktick.xml", "ticktick_unselected.xml"),
    Story("speech_bubble.xml", "speech_bubble_unselected.xml"),
}
