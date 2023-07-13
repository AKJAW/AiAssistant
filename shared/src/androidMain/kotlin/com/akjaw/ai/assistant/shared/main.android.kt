package com.akjaw.ai.assistant.shared

import androidx.compose.runtime.Composable
import com.akjaw.ai.assistant.shared.dashboard.domain.ChatType

actual fun getPlatformName(): String = "Android"

@Composable fun MainView(initialType: ChatType?) = App(initialType)
