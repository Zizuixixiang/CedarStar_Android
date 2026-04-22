package org.cedarstar.android.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class SendMessageRequest(
    val content: String,
)
