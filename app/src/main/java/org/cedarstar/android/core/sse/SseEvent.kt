package org.cedarstar.android.core.sse

import org.cedarstar.android.data.model.ChatMessage
import org.cedarstar.android.data.model.Emotion

sealed class SseEvent {
    data class StatusUpdate(
        val pocketMoney: Double,
        val emotion: Emotion,
        val currentMode: String,
    ) : SseEvent()

    data class ConnectionUpdate(
        val platform: String,
        val status: String,
    ) : SseEvent()

    data class ChatMsg(
        val message: ChatMessage,
    ) : SseEvent()

    data class Unknown(
        val rawType: String,
        val rawData: String,
    ) : SseEvent()
}
