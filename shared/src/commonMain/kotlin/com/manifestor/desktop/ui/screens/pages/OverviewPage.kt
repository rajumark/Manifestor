package com.manifestor.desktop.ui.screens.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.manifestor.desktop.ProjectInfo

@Composable
fun OverviewPage(
    projectInfo: ProjectInfo,
    modifier: Modifier = Modifier,
) {
    val scheme = MaterialTheme.colorScheme

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Overview — Coming soon",
            style = MaterialTheme.typography.titleLarge,
            color = scheme.onSurfaceVariant,
        )
    }
}
