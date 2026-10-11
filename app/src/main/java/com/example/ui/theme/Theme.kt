package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LedgerDarkColorScheme = darkColorScheme(
    primary = LedgerPurpleAccent,
    onPrimary = LedgerBlack,
    primaryContainer = LedgerPillDark,
    onPrimaryContainer = LedgerTextPrimary,
    secondary = LedgerPurpleSlider,
    onSecondary = LedgerTextPrimary,
    background = LedgerBlack,
    onBackground = LedgerTextPrimary,
    surface = LedgerSurface,
    onSurface = LedgerTextPrimary,
    surfaceVariant = LedgerCard,
    onSurfaceVariant = LedgerTextSecondary,
    outline = LedgerCardBorder,
    error = LedgerRed,
    onError = LedgerTextPrimary
)

@Composable
fun LedgerTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            window.statusBarColor = LedgerBlack.toArgb()
            window.navigationBarColor = LedgerBlack.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = LedgerDarkColorScheme,
        typography = LedgerTypography,
        content = content
    )
}
