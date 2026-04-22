package org.cedarstar.android.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.Extension
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.ui.graphics.vector.ImageVector

sealed class CedarDestination(val route: String, val label: String, val icon: ImageVector) {
    data object Chat : CedarDestination("chat", "消息", Icons.Outlined.Chat)
    data object Journal : CedarDestination("journal", "共记", Icons.Outlined.MenuBook)
    data object Companion : CedarDestination("companion", "共玩", Icons.Outlined.Extension)
    data object Clio : CedarDestination("clio", "小克", Icons.Outlined.Pets)
    data object Dashboard : CedarDestination("dashboard", "Dashboard", Icons.Outlined.MenuBook)
}
