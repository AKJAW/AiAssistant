package com.akjaw.ai.assistant.android

import com.akjaw.ai.assistant.shared.MainView
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.akjaw.ai.assistant.BuildConfig
import com.akjaw.ai.assistant.shared.chat.data.database.ProductionDriverFactory
import com.akjaw.ai.assistant.shared.composition.initializeDependencies
import com.akjaw.ai.assistant.shared.dashboard.domain.ChatType
import com.akjaw.ai.assistant.shared.utils.BuildInfo

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        initializeDependencies(
            isDebug = BuildConfig.DEBUG,
            driverFactory = ProductionDriverFactory(applicationContext)
        )
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