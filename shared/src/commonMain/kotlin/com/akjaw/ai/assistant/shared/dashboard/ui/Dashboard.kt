package com.akjaw.ai.assistant.shared.dashboard.ui

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.akjaw.ai.assistant.shared.chat.domain.ChatMessageHandler
import com.akjaw.ai.assistant.shared.chat.domain.createKtorClient
import com.akjaw.ai.assistant.shared.chat.presentation.ChatScreenStateHolder
import com.akjaw.ai.assistant.shared.chat.ui.ChatScreen
import com.akjaw.ai.assistant.shared.dashboard.domain.ChatType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalFoundationApi::class)
class DashboardScreenStateHolder(
    private val scope: CoroutineScope,
    private val pagerState: PagerState,
    initialType: ChatType = ChatType.Notion,
    private val chatScreenStateHolder: ChatMessageHandler = ChatMessageHandler(createKtorClient()),
) {

    var stateHolders: List<ChatScreenStateHolder> by mutableStateOf(
        ChatType.values().map { type ->
            ChatScreenStateHolder(type, scope, chatScreenStateHolder)
        }
    )

    var currentChatType: ChatType by mutableStateOf(initialType)
        private set

    fun setType(newChatType: ChatType) {
        val index = stateHolders.indexOfFirst { it.type == newChatType }
        scope.launch {
            pagerState.scrollToPage(index)
        }
        currentChatType = newChatType
    }

    companion object {

        @Composable
        fun remember(
            coroutineScope: CoroutineScope,
            pagerState: PagerState
        ): DashboardScreenStateHolder {
            val type = "type"
            return rememberSaveable(
                saver = mapSaver(
                    save = { holder: DashboardScreenStateHolder ->
                        mapOf(type to holder.currentChatType)
                    },
                    restore = {
                        DashboardScreenStateHolder(coroutineScope, pagerState, it[type] as ChatType)
                    }
                )
            ) {
                DashboardScreenStateHolder(coroutineScope, pagerState)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DashboardScreen(initialType: ChatType?) {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState()
    val dashboardScreenStateHolder = DashboardScreenStateHolder.remember(scope, pagerState)
    LaunchedEffect(initialType) {
        if (initialType != null) dashboardScreenStateHolder.setType(initialType)
    }
    Column(modifier = Modifier.padding(horizontal = 8.dp)) {
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            ChatType.values().forEach { type ->
                TypeButton(
                    chatType = type,
                    onClick = { dashboardScreenStateHolder.setType(type) },
                    isSelected = dashboardScreenStateHolder.currentChatType == type
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        HorizontalPager(
            pageCount = dashboardScreenStateHolder.stateHolders.count(),
            state = pagerState,
            userScrollEnabled = false,
        ) { pageNumber ->
            val stateHolder = dashboardScreenStateHolder.stateHolders[pageNumber]
            ChatScreen(stateHolder)
        }
        Spacer(modifier = Modifier.height(4.dp))
    }
}

@Composable
private fun TypeButton(
    chatType: ChatType,
    onClick: () -> Unit,
    isSelected: Boolean,
) {
    val borderColor = remember(isSelected) { if (isSelected) Color.Black else Color.Gray }
    val icon = remember(isSelected) {
        if (isSelected) chatType.selectedResource else chatType.unselectedResource
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
