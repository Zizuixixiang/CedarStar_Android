package org.cedarstar.android.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class StatusUpdateDto(
    val pocketMoney: Double,
    val emotion: String,
    val currentMode: String,
)
