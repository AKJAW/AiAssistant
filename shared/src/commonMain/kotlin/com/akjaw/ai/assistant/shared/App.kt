@file:OptIn(ExperimentalResourceApi::class)

package com.akjaw.ai.assistant.shared

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.akjaw.ai.assistant.shared.chat.presentation.ChatScreenStateHolder
import com.akjaw.ai.assistant.shared.chat.ui.ChatScreen
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource

@Composable
fun App() {
    MaterialTheme(colors = MaterialTheme.colors.copy(primary = Color(0xFF4E7DF8))) {
        val scope = rememberCoroutineScope()
        Column(modifier = Modifier.padding(horizontal = 8.dp)) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                TypeButton(
                    selectedResource = "notion_logo.xml",
                    unselectedResource = "notion_logo_unselected.xml",
                    isSelected = true
                )
                TypeButton(
                    selectedResource = "ticktick.xml",
                    unselectedResource = "ticktick_unselected.xml",
                    isSelected = false
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            ChatScreen(remember { ChatScreenStateHolder(scope) })
        }
    }
}

@Composable
private fun TypeButton(
    selectedResource: String,
    unselectedResource: String,
    isSelected: Boolean
) {
    val borderColor = remember(isSelected) { if (isSelected) Color.Black else Color.Gray }
    val icon = remember(isSelected) { if (isSelected) selectedResource else unselectedResource }
    Box(
        Modifier.border(1.dp, borderColor, RoundedCornerShape(size = 4.dp)).padding(4.dp)
    ) {
        Image(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier.size(48.dp)
        )
    }
}


expect fun getPlatformName(): String