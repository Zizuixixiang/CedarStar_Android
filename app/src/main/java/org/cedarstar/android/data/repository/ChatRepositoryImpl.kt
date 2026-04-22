package org.cedarstar.android.data.repository

import javax.inject.Inject
import org.cedarstar.android.data.api.CedarStarApi
import org.cedarstar.android.data.model.ChatMessage

class ChatRepositoryImpl @Inject constructor(
    private val api: CedarStarApi,
) : ChatRepository {
    override suspend fun history(limit: Int, before: Long?): List<ChatMessage> = emptyList()
    override suspend fun sendMessage(content: String) = Unit
}
