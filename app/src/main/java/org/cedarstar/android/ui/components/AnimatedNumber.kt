package org.cedarstar.android.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.material3.Text
import org.cedarstar.android.util.formatMoney

@Composable
fun AnimatedNumber(value: Double) {
    val animated = remember { Animatable(value.toFloat()) }
    LaunchedEffect(value) {
        animated.animateTo(value.toFloat(), tween(250, easing = FastOutSlowInEasing))
    }
    Text("¥${animated.value.formatMoney()}")
}
