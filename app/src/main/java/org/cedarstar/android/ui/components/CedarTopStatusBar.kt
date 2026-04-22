package org.cedarstar.android.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.cedarstar.android.data.model.AppStatus

@Composable
fun CedarTopStatusBar(appStatus: AppStatus) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        AnimatedNumber(appStatus.pocketMoney)
        Spacer(modifier = Modifier.width(4.dp))
        AnimatedEmotionIcon(appStatus.emotion)
        Text(appStatus.currentMode)
    }
}
