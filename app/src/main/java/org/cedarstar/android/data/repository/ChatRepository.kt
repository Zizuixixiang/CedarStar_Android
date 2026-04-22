package org.cedarstar.android.data.repository

import org.cedarstar.android.data.model.ChatMessage

interface ChatRepository {
    suspend fun history(limit: Int, before: Long? = null): List<ChatMessage>
    suspend fun sendMessage(content: String)
}
