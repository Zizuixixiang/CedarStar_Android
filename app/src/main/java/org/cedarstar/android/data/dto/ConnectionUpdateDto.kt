package org.cedarstar.android.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class ConnectionUpdateDto(
    val platform: String,
    val status: String,
)
