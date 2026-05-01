package org.cedarstar.android.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.cedarstar.android.data.model.Emotion

@Composable
fun AnimatedEmotionIcon(emotion: Emotion) {
    AnimatedContent(targetState = emotion, label = "emotion", transitionSpec = { fadeIn() togetherWith fadeOut() }) { target ->
        Text(target.emoji)
    }
}
