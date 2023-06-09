package com.akjaw.ai.assistant.shared

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.akjaw.ai.assistant.shared.dashboard.domain.ChatType
import com.akjaw.ai.assistant.shared.dashboard.ui.DashboardScreen
import com.akjaw.ai.assistant.shared.utils.BuildInfo

val color = if (BuildInfo.isDebug) Color(0xFFC6433C) else Color(0xFF4E7DF8)

@Composable
fun App(initialType: ChatType? = null) {
    MaterialTheme(colors = MaterialTheme.colors.copy(primary = color)) {
        DashboardScreen(initialType)
    }
}

expect fun getPlatformName(): String
