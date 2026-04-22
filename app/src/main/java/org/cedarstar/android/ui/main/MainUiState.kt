package org.cedarstar.android.ui.main

import org.cedarstar.android.data.model.AppStatus
import org.cedarstar.android.data.model.PlatformConnection
import org.cedarstar.android.ui.navigation.CedarDestination

data class MainUiState(
    val appStatus: AppStatus,
    val connections: List<PlatformConnection>,
    val currentDestination: CedarDestination,
    val isDrawerOpen: Boolean,
    val isDashboardOpen: Boolean,
) {
    companion object {
        val Empty = MainUiState(
            appStatus = AppStatus.Empty,
            connections = emptyList(),
            currentDestination = CedarDestination.Chat,
            isDrawerOpen = false,
            isDashboardOpen = false,
        )
    }
}
