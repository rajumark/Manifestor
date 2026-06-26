package com.manifestor.desktop.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
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
    onBack: () -> Unit,
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
            Spacer(Modifier.height(32.dp))
            Button(onClick = onBack) {
                Text("Back")
            }
        }
    }
}
