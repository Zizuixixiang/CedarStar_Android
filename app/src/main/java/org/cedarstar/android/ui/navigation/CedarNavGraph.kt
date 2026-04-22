package org.cedarstar.android.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import org.cedarstar.android.ui.chat.ChatScreen
import org.cedarstar.android.ui.chat.ChatViewModel
import org.cedarstar.android.ui.clio.ClioScreen
import org.cedarstar.android.ui.clio.ClioViewModel
import org.cedarstar.android.ui.companion.CompanionScreen
import org.cedarstar.android.ui.companion.CompanionViewModel
import org.cedarstar.android.ui.dashboard.DashboardScreen
import org.cedarstar.android.ui.dashboard.DashboardViewModel
import org.cedarstar.android.ui.journal.JournalScreen
import org.cedarstar.android.ui.journal.JournalViewModel

@Composable
fun CedarNavGraph(navController: NavHostController, startDestination: String = CedarDestination.Chat.route) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable(CedarDestination.Chat.route) { ChatScreen(hiltViewModel<ChatViewModel>()) }
        composable(CedarDestination.Journal.route) { JournalScreen(hiltViewModel<JournalViewModel>()) }
        composable(CedarDestination.Companion.route) { CompanionScreen(hiltViewModel<CompanionViewModel>()) }
        composable(CedarDestination.Clio.route) { ClioScreen(hiltViewModel<ClioViewModel>()) }
        composable(CedarDestination.Dashboard.route) { DashboardScreen(hiltViewModel<DashboardViewModel>()) }
    }
}
