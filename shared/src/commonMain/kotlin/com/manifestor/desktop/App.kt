package com.manifestor.desktop

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.manifestor.desktop.ui.screens.WelcomeScreen
import com.manifestor.desktop.ui.theme.ThemeOption

@Composable
fun App(
    apkPath: String? = null,
    isDragging: Boolean = false,
    projectName: String = "",
    onProjectNameChange: (String) -> Unit = {},
    onCreateProject: () -> Unit = {},
    errorMessage: String? = null,
    errorDialogMessage: String? = null,
    onDismissErrorDialog: () -> Unit = {},
    themeOption: ThemeOption = ThemeOption.DARK,
    onSettingsClick: () -> Unit = {},
    onBrowseClick: () -> Unit = {},
    onClearApk: () -> Unit = {},
) {
    val isDark = when (themeOption) {
        ThemeOption.DARK -> true
        ThemeOption.LIGHT -> false
        ThemeOption.SYSTEM -> isSystemInDarkTheme()
    }

    MaterialTheme(
        colorScheme = if (isDark) darkColorScheme() else lightColorScheme(),
    ) {
        WelcomeScreen(
            modifier = Modifier.fillMaxSize(),
            apkPath = apkPath,
            isDragging = isDragging,
            projectName = projectName,
            onProjectNameChange = onProjectNameChange,
            onCreateProject = onCreateProject,
            errorMessage = errorMessage,
            onSettingsClick = onSettingsClick,
            onBrowseClick = onBrowseClick,
            onClearApk = onClearApk,
        )

        if (errorDialogMessage != null) {
            AlertDialog(
                onDismissRequest = onDismissErrorDialog,
                title = { Text("Error") },
                text = { Text(errorDialogMessage) },
                confirmButton = {
                    TextButton(onClick = onDismissErrorDialog) {
                        Text("OK")
                    }
                },
            )
        }
    }
}
