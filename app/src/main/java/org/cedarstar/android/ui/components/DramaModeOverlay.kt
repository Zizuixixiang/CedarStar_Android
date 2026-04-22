package org.cedarstar.android.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun DramaModeOverlay(isVisible: Boolean) {
    AnimatedVisibility(visible = isVisible) {
        Text("Drama Mode")
    }
}
