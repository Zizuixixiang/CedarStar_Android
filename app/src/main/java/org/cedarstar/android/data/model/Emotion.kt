package org.cedarstar.android.data.model

enum class Emotion(val raw: String, val emoji: String) {
    HappyDog("happy_dog", "🐶"),
    Calm("calm", "😌"),
    Focus("focus", "🧠"),
    Sad("sad", "😢"),
    Unknown("unknown", "❔");

    companion object {
        fun fromRaw(raw: String): Emotion = entries.firstOrNull { it.raw == raw } ?: Unknown
    }
}
