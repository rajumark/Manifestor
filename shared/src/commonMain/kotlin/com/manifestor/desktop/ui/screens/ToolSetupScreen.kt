package com.manifestor.desktop.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.manifestor.desktop.ToolSetupState

@Composable
fun ToolSetupScreen(
    state: ToolSetupState,
    progress: Float,
    errorMessage: String?,
    onDownload: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scheme = MaterialTheme.colorScheme

    Box(
        modifier = modifier.fillMaxSize().background(scheme.background).padding(32.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "Tool Setup",
                style = MaterialTheme.typography.displayMedium,
                color = scheme.onSurface,
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = "The following tools need to be downloaded:",
                style = MaterialTheme.typography.bodyLarge,
                color = scheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "\u2022 jadx-1.5.5 (APK decompiler)",
                style = MaterialTheme.typography.bodyMedium,
                color = scheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(32.dp))

            when (state) {
                ToolSetupState.NOT_STARTED -> {
                    Button(onClick = onDownload) {
                        Text("Download & Setup")
                    }
                }
                ToolSetupState.DOWNLOADING -> {
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.width(300.dp),
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.bodyMedium,
                        color = scheme.onSurfaceVariant,
                    )
                }
                ToolSetupState.EXTRACTING -> {
                    LinearProgressIndicator(
                        modifier = Modifier.width(300.dp),
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Extracting...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = scheme.onSurfaceVariant,
                    )
                }
                ToolSetupState.ERROR -> {
                    Text(
                        text = errorMessage ?: "Download failed",
                        style = MaterialTheme.typography.bodyMedium,
                        color = scheme.error,
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = onDownload) {
                        Text("Retry")
                    }
                }
                ToolSetupState.COMPLETED -> { }
            }
        }
    }
}
