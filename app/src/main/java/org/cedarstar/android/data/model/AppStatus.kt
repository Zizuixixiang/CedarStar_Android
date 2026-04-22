package org.cedarstar.android.data.model

data class AppStatus(
    val pocketMoney: Double,
    val emotion: Emotion,
    val currentMode: String,
) {
    companion object {
        val Empty = AppStatus(0.0, Emotion.Unknown, "normal")
    }
}
