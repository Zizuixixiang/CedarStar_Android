package org.cedarstar.android.core.sse

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.cedarstar.android.data.dto.ChatMsgDto
import org.cedarstar.android.data.dto.ConnectionUpdateDto
import org.cedarstar.android.data.dto.StatusUpdateDto
import org.cedarstar.android.data.model.Emotion

class SseEventParser(
    private val json: Json,
) {
    fun parse(payload: String): SseEvent {
        return runCatching {
            val root = json.parseToJsonElement(payload).jsonObject
            val type = root["type"]?.jsonPrimitive?.content.orEmpty()
            val data = root["data"]?.jsonObject
            when (type) {
                "status_update" -> {
                    val dto = json.decodeFromJsonElement(StatusUpdateDto.serializer(), data!!)
                    SseEvent.StatusUpdate(dto.pocketMoney, Emotion.fromRaw(dto.emotion), dto.currentMode)
                }
                "connection_update" -> {
                    val dto = json.decodeFromJsonElement(ConnectionUpdateDto.serializer(), data!!)
                    SseEvent.ConnectionUpdate(dto.platform, dto.status)
                }
                "chat_msg" -> {
                    val dto = json.decodeFromJsonElement(ChatMsgDto.serializer(), data!!)
                    SseEvent.ChatMsg(dto.toModel())
                }
                else -> SseEvent.Unknown(type, payload)
            }
        }.getOrElse {
            SseEvent.Unknown("parse_error", payload)
        }
    }
}
