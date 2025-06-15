package com.android.kosheroverlay.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = md3_primary,
    onPrimary = md3_on_primary,
    primaryContainer = md3_primary_container,
    onPrimaryContainer = md3_on_primary_container,
    secondary = md3_secondary,
    onSecondary = md3_on_secondary,
    secondaryContainer = md3_secondary_container,
    onSecondaryContainer = md3_on_secondary_container,
    surface = md3_surface,
    onSurface = md3_on_surface,
    surfaceContainer = md3_surface_container,
    onSurfaceVariant = md3_on_surface_variant,
    background = md3_background
)

@Composable
fun KosherOverlayTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}