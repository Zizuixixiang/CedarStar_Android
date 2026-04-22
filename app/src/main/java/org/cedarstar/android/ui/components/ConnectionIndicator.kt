package org.cedarstar.android.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.map
import org.cedarstar.android.ui.main.MainViewModel

@Composable
fun ConnectionIndicator(platform: String, viewModel: MainViewModel) {
    val connection by viewModel.uiState
        .map { state -> state.connections.find { it.platform == platform } }
        .collectAsStateWithLifecycle(initialValue = null)
    Text(if (connection?.isConnected == true) "●" else "○")
}
