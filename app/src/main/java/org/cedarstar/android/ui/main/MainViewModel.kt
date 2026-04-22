package org.cedarstar.android.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import org.cedarstar.android.data.repository.ConnectionRepository
import org.cedarstar.android.data.repository.StatusRepository
import org.cedarstar.android.ui.navigation.CedarDestination

@HiltViewModel
class MainViewModel @Inject constructor(
    statusRepository: StatusRepository,
    connectionRepository: ConnectionRepository,
) : ViewModel() {
    private val currentDestination = MutableStateFlow<CedarDestination>(CedarDestination.Chat)
    private val isDrawerOpen = MutableStateFlow(false)
    private val isDashboardOpen = MutableStateFlow(false)

    val uiState: StateFlow<MainUiState> = combine(
        statusRepository.appStatusFlow,
        connectionRepository.connectionsFlow,
        currentDestination,
        isDrawerOpen,
        isDashboardOpen,
    ) { appStatus, connections, destination, drawerOpen, dashboardOpen ->
        MainUiState(
            appStatus = appStatus,
            connections = connections,
            currentDestination = destination,
            isDrawerOpen = drawerOpen,
            isDashboardOpen = dashboardOpen,
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, MainUiState.Empty)

    fun selectDestination(destination: CedarDestination) {
        currentDestination.value = destination
    }

    fun setDrawerOpen(open: Boolean) {
        isDrawerOpen.value = open
    }

    fun setDashboardOpen(open: Boolean) {
        isDashboardOpen.value = open
    }
}
