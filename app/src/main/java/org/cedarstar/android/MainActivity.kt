package org.cedarstar.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import org.cedarstar.android.ui.main.MainScreen
import org.cedarstar.android.ui.theme.CedarStarTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CedarStarTheme {
                MainScreen()
            }
        }
    }
}
