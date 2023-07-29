package com.akjaw.ai.assistant.shared

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.akjaw.ai.assistant.shared.dashboard.domain.ChatType

actual fun getPlatformName(): String = "Android"

@Composable
fun MainView(initialType: ChatType?) {
    Box(Modifier.safeDrawingPadding()) {
        App(initialType)
    }
}
