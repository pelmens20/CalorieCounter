package com.example.caloriecounter.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = ForestGreen,
    primaryContainer = DeepForest,
    onPrimary = IvoryWhite,
    secondary = SunGold,
    secondaryContainer = AmberGlow,
    onSecondary = MidnightBlack,
    background = IvoryWhite,
    onBackground = MidnightBlack,
    surface = IvoryWhite,
    onSurface = MidnightBlack,
    error = ErrorRed
)

@Composable
fun CalorieCounterTheme(content: @Composable () -> Unit) {
    val colorScheme = LightColorScheme
    val view = LocalView.current
    val context = LocalContext.current
    val activity = context as? Activity

    SideEffect {
        activity?.window?.let { window ->
            window.statusBarColor = colorScheme.primaryContainer.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

object CalorieCounterTheme {
    val context @Composable get() = LocalContext.current
}