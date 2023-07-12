package com.akjaw.ai.assistant.shared.chat.data.time

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class TimestampMilliseconds(val value: Long)

interface TimestampProvider {

    fun getMilliseconds(): TimestampMilliseconds
}

internal class KotlinXTimestampProvider : TimestampProvider {

    override fun getMilliseconds(): TimestampMilliseconds =
        TimestampMilliseconds(Clock.System.now().toEpochMilliseconds())

}