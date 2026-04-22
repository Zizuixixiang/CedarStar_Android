package org.cedarstar.android.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = CedarPrimary,
    secondary = CedarSecondary,
    tertiary = CedarTertiary,
    background = CedarBackground,
    surface = CedarSurface,
)

private val DarkColors = darkColorScheme(
    primary = CedarPrimary,
    secondary = CedarSecondary,
    tertiary = CedarTertiary,
)

@Composable
fun CedarStarTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = CedarTypography,
        content = content,
    )
}
