package org.cedarstar.android.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.cedarstar.android.ui.main.MainViewModel

@Composable
fun CedarDrawer(viewModel: MainViewModel, drawerState: DrawerState) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text("会话")
        Text("人设")
        listOf("telegram", "discord", "wechat").forEach { platform ->
            ConnectionIndicator(platform, viewModel)
        }
    }
}
