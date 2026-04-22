package org.cedarstar.android.data.repository

import javax.inject.Inject
import org.cedarstar.android.data.model.ChatMessage

class ChatRepositoryMock @Inject constructor() : ChatRepository {
    override suspend fun history(limit: Int, before: Long?): List<ChatMessage> = listOf(
        ChatMessage("1", "Mock 历史消息 1", 1713800000),
        ChatMessage("2", "Mock 历史消息 2", 1713800060),
        ChatMessage("3", "Mock 历史消息 3", 1713800120),
    )

    override suspend fun sendMessage(content: String) = Unit
}
