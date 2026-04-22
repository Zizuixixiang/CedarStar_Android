package org.cedarstar.android.ui.components

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.cedarstar.android.ui.navigation.BottomNavItems
import org.cedarstar.android.ui.navigation.CedarDestination

@Composable
fun CedarBottomBar(selected: CedarDestination, onSelect: (CedarDestination) -> Unit) {
    NavigationBar {
        BottomNavItems.items.forEach { destination ->
            NavigationBarItem(
                selected = selected.route == destination.route,
                onClick = { onSelect(destination) },
                icon = { Text(destination.label.first().toString()) },
                label = { Text(destination.label) },
            )
        }
    }
}
