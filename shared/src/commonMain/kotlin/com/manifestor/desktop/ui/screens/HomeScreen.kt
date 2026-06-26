package com.manifestor.desktop.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.manifestor.desktop.ProjectInfo

@Composable
fun HomeScreen(
    projectInfo: ProjectInfo,
    isDecompiling: Boolean = false,
    decompileError: String? = null,
    decompileProgress: Float = 0f,
    decompileStatusText: String = "",
    onRetryDecompile: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val scheme = MaterialTheme.colorScheme

    Box(
        modifier = modifier.fillMaxSize().background(scheme.background),
    ) {
        if (isDecompiling) {
            Column(
                modifier = Modifier.fillMaxSize().padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                CircularProgressIndicator()
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Decompiling APK...",
                    style = MaterialTheme.typography.bodyLarge,
                    color = scheme.onSurfaceVariant,
                )
                if (decompileProgress > 0f) {
                    Spacer(Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = { decompileProgress },
                        modifier = Modifier.width(300.dp),
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = decompileStatusText,
                        style = MaterialTheme.typography.bodySmall,
                        color = scheme.onSurfaceVariant,
                    )
                }
            }
        } else if (decompileError != null) {
            Column(
                modifier = Modifier.fillMaxSize().padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "Decompilation failed",
                    style = MaterialTheme.typography.titleMedium,
                    color = scheme.error,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = decompileError,
                    style = MaterialTheme.typography.bodySmall,
                    color = scheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(16.dp))
                Button(onClick = onRetryDecompile) {
                    Text("Retry")
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize().padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = projectInfo.projectName,
                    style = MaterialTheme.typography.displayMedium,
                    color = scheme.onSurface,
                )
                Spacer(Modifier.height(24.dp))
                Text(
                    text = "APK: ${projectInfo.apkFileName}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = scheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Path: ${projectInfo.apkFullPath}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = scheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Created: ${projectInfo.createdAt}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = scheme.onSurfaceVariant,
                )
                if (decompileStatusText.isNotEmpty()) {
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = decompileStatusText,
                        style = MaterialTheme.typography.labelMedium,
                        color = scheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}
