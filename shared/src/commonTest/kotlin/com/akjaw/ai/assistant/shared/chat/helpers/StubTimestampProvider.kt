package com.akjaw.ai.assistant.shared.chat.helpers

import com.akjaw.ai.assistant.shared.chat.data.time.TimestampMilliseconds
import com.akjaw.ai.assistant.shared.chat.data.time.TimestampProvider

class StubTimestampProvider : TimestampProvider {

    var value: Long = -1L

    override fun getMilliseconds(): TimestampMilliseconds = TimestampMilliseconds(value)
}