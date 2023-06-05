package com.akjaw.ai.assistant.shared.dashboard.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.akjaw.ai.assistant.shared.chat.presentation.ChatScreenStateHolder
import com.akjaw.ai.assistant.shared.chat.ui.ChatScreen
import org.jetbrains.compose.resources.painterResource

class DashboardScreenStateHolder {

    enum class Type(val selectedResource: String, val unselectedResource: String) {
        Notion("notion_logo.xml", "notion_logo_unselected.xml"),
        TickTick("ticktick.xml", "ticktick_unselected.xml"),
    }

    var currentType: Type by mutableStateOf(Type.Notion)
        private set

    fun setType(newType: Type) {
        currentType = newType
    }
}

@Composable
fun DashboardScreen() {
    val scope = rememberCoroutineScope()
    val dashboardScreenStateHolder = remember { DashboardScreenStateHolder() }
    Column(modifier = Modifier.padding(horizontal = 8.dp)) {
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            DashboardScreenStateHolder.Type.values().forEach { type ->
                TypeButton(
                    type = type,
                    onClick = { dashboardScreenStateHolder.setType(type) },
                    isSelected = dashboardScreenStateHolder.currentType == type
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        ChatScreen(remember { ChatScreenStateHolder(scope) })
    }
}

@Composable
private fun TypeButton(
    type: DashboardScreenStateHolder.Type,
    onClick: () -> Unit,
    isSelected: Boolean,
) {
    val borderColor = remember(isSelected) { if (isSelected) Color.Black else Color.Gray }
    val icon = remember(isSelected) {
        if (isSelected) type.selectedResource else type.unselectedResource
    }
    Box(
        Modifier
            .clickable(
                onClick = onClick,
                indication = null,
                interactionSource = MutableInteractionSource()
            )
            .border(1.dp, borderColor, RoundedCornerShape(size = 4.dp))
            .padding(4.dp),
    ) {
        Image(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier.size(48.dp)
        )
    }
}
