package org.cedarstar.android.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import org.cedarstar.android.ui.components.CedarBottomBar
import org.cedarstar.android.ui.components.CedarDrawer
import org.cedarstar.android.ui.components.CedarTopStatusBar
import org.cedarstar.android.ui.navigation.CedarDestination
import org.cedarstar.android.ui.navigation.CedarNavGraph

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val navController = rememberNavController()

    LaunchedEffect(uiState.isDrawerOpen) {
        if (uiState.isDrawerOpen) drawerState.open() else drawerState.close()
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = { ModalDrawerSheet { CedarDrawer(viewModel = viewModel, drawerState = drawerState) } },
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { CedarTopStatusBar(uiState.appStatus) },
                    navigationIcon = { IconButton(onClick = { viewModel.setDrawerOpen(true) }) { Text("≡") } },
                    actions = { IconButton(onClick = { viewModel.setDashboardOpen(true) }) { Text("⊞") } },
                )
            },
            bottomBar = {
                CedarBottomBar(selected = uiState.currentDestination) { destination ->
                    viewModel.selectDestination(destination)
                    navController.navigate(destination.route) { launchSingleTop = true }
                }
            },
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                CedarNavGraph(navController = navController, startDestination = uiState.currentDestination.route)
            }
        }
    }
}
