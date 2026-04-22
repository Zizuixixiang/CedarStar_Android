package org.cedarstar.android.data.dto

import kotlinx.serialization.Serializable
import org.cedarstar.android.data.model.ChatMessage

@Serializable
data class ChatMsgDto(
    val msgId: String,
    val content: String,
    val timestamp: Long,
) {
    fun toModel(): ChatMessage = ChatMessage(
        id = msgId,
        content = content,
        timestamp = timestamp,
    )
}
