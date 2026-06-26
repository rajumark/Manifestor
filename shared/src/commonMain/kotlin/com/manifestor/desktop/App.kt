package com.manifestor.desktop

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.manifestor.desktop.ui.screens.WelcomeScreen
import com.manifestor.desktop.ui.theme.AppColors
import com.manifestor.desktop.ui.theme.AppTypography

@Composable
fun App() {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = AppColors.accent,
            surface = AppColors.surface,
            background = AppColors.background,
            onPrimary = AppColors.textPrimary,
            onSurface = AppColors.textPrimary,
            onBackground = AppColors.textPrimary,
        ),
        typography = AppTypography.typography,
    ) {
        WelcomeScreen(
            modifier = Modifier.fillMaxSize(),
        )
    }
}
