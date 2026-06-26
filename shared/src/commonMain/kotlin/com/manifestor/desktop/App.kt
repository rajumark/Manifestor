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
import com.manifestor.desktop.ui.screens.HomeScreen
import com.manifestor.desktop.ui.screens.ToolSetupScreen
import com.manifestor.desktop.ui.screens.WelcomeScreen
import com.manifestor.desktop.ui.theme.ThemeOption

@Composable
fun App(
    screen: Screen = Screen.TOOL_SETUP,
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
    projectInfo: ProjectInfo? = null,
    onNavigateHome: () -> Unit = {},
    onNavigateWelcome: () -> Unit = {},
    toolSetupState: ToolSetupState = ToolSetupState.NOT_STARTED,
    downloadProgress: Float = 0f,
    toolSetupError: String? = null,
    onToolDownload: () -> Unit = {},
    isDecompiling: Boolean = false,
    decompileError: String? = null,
    decompileProgress: Float = 0f,
    decompileStatusText: String = "",
    onRetryDecompile: () -> Unit = {},
    projects: List<ProjectSummary> = emptyList(),
    onProjectClick: (ProjectSummary) -> Unit = {},
    sourceCodeEntries: List<FileEntry> = emptyList(),
    sourceCodeCurrentPath: String = "",
    sourceCodeSelectedFile: String? = null,
    sourceCodeFileContent: String = "",
    onSourceCodeNavigate: (String) -> Unit = {},
    onSourceCodeBack: () -> Unit = {},
    onSourceCodeFileClick: (String) -> Unit = {},
    onSourceCodeDownload: () -> Unit = {},
    onSourceCodeCopy: () -> Unit = {},
    onSourceCodeSearchFile: (String) -> Unit = {},
    onSourceCodeSearchContent: (String) -> Unit = {},
) {
    val isDark = when (themeOption) {
        ThemeOption.DARK -> true
        ThemeOption.LIGHT -> false
        ThemeOption.SYSTEM -> isSystemInDarkTheme()
    }

    MaterialTheme(
        colorScheme = if (isDark) darkColorScheme() else lightColorScheme(),
    ) {
        when (screen) {
            Screen.TOOL_SETUP -> {
                ToolSetupScreen(
                    state = toolSetupState,
                    progress = downloadProgress,
                    errorMessage = toolSetupError,
                    onDownload = onToolDownload,
                    modifier = Modifier.fillMaxSize(),
                )
            }
            Screen.WELCOME -> {
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
                    projects = projects,
                    onProjectClick = onProjectClick,
                    showBackButton = projectInfo != null,
                    onBackClick = onNavigateHome,
                )
            }
            Screen.HOME -> {
                if (projectInfo != null) {
                    HomeScreen(
                        projectInfo = projectInfo,
                        isDecompiling = isDecompiling,
                        decompileError = decompileError,
                        decompileProgress = decompileProgress,
                        decompileStatusText = decompileStatusText,
                        onRetryDecompile = onRetryDecompile,
                        onSettingsClick = onSettingsClick,
                        onTitleClick = onNavigateWelcome,
                        sourceCodeEntries = sourceCodeEntries,
                        sourceCodeCurrentPath = sourceCodeCurrentPath,
                        sourceCodeSelectedFile = sourceCodeSelectedFile,
                        sourceCodeFileContent = sourceCodeFileContent,
                        onSourceCodeNavigate = onSourceCodeNavigate,
                        onSourceCodeBack = onSourceCodeBack,
                        onSourceCodeFileClick = onSourceCodeFileClick,
                        onSourceCodeDownload = onSourceCodeDownload,
                        onSourceCodeCopy = onSourceCodeCopy,
                        onSourceCodeSearchFile = onSourceCodeSearchFile,
                        onSourceCodeSearchContent = onSourceCodeSearchContent,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }

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
