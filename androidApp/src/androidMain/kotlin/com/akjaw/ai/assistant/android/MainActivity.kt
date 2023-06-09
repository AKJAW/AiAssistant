package com.akjaw.ai.assistant.android

import com.akjaw.ai.assistant.shared.MainView
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.akjaw.ai.assistant.BuildConfig
import com.akjaw.ai.assistant.shared.chat.data.database.ProductionDriverFactory
import com.akjaw.ai.assistant.shared.chat.data.database.createDatabase
import com.akjaw.ai.assistant.shared.dashboard.domain.ChatType
import com.akjaw.ai.assistant.shared.utils.BuildInfo

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        BuildInfo.isDebug = BuildConfig.DEBUG
        createDatabase(ProductionDriverFactory(context = applicationContext))
        val type = intent.getStringExtra("chatType")?.toType()
        setContent {
            MainView(type)
        }
    }

    fun String.toType(): ChatType? = when (this) {
        "notion" -> ChatType.Notion
        "ticktick" -> ChatType.TickTick
        else -> null
    }
}