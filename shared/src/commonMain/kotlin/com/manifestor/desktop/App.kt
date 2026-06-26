package com.manifestor.desktop

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.manifestor.desktop.ui.screens.WelcomeScreen

@Composable
fun App(
    apkPath: String? = null,
    isDragging: Boolean = false,
    projectName: String = "",
    onProjectNameChange: (String) -> Unit = {},
    onCreateProject: () -> Unit = {},
    errorMessage: String? = null,
    onBrowseClick: () -> Unit = {},
    onClearApk: () -> Unit = {},
) {
    MaterialTheme(
        colorScheme = darkColorScheme(),
    ) {
        WelcomeScreen(
            modifier = Modifier.fillMaxSize(),
            apkPath = apkPath,
            isDragging = isDragging,
            projectName = projectName,
            onProjectNameChange = onProjectNameChange,
            onCreateProject = onCreateProject,
            errorMessage = errorMessage,
            onBrowseClick = onBrowseClick,
            onClearApk = onClearApk,
        )
    }
}
