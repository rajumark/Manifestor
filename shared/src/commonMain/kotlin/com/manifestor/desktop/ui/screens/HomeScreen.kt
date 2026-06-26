package com.manifestor.desktop.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.manifestor.desktop.ProjectInfo
import com.manifestor.desktop.ui.screens.pages.ManifestPage
import com.manifestor.desktop.ui.screens.pages.OverviewPage
import com.manifestor.desktop.ui.screens.pages.SourceCodePage

enum class SidebarPage { OVERVIEW, MANIFEST, SOURCE_CODE }

private data class NavItem(
    val page: SidebarPage,
    val icon: String?,
    val label: String,
)

private val navItems = listOf(
    NavItem(SidebarPage.OVERVIEW, "\uD83D\uDCCB", "Overview"),
    NavItem(SidebarPage.MANIFEST, "\uD83D\uDCC4", "Manifest"),
    NavItem(SidebarPage.SOURCE_CODE, null, "Source Code"),
)

@Composable
fun HomeScreen(
    projectInfo: ProjectInfo,
    isDecompiling: Boolean = false,
    decompileError: String? = null,
    decompileProgress: Float = 0f,
    decompileStatusText: String = "",
    onRetryDecompile: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val scheme = MaterialTheme.colorScheme
    var selectedPage by remember { mutableStateOf(SidebarPage.OVERVIEW) }

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
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier.fillMaxWidth().height(56.dp).background(scheme.surface)
                        .padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = { }) {
                        Text(
                            text = "\u2630",
                            style = MaterialTheme.typography.titleLarge,
                            color = scheme.onSurface,
                        )
                    }
                    Text(
                        text = projectInfo.projectName,
                        style = MaterialTheme.typography.titleLarge,
                        color = scheme.onSurface,
                        modifier = Modifier.weight(1f),
                    )
                    IconButton(onClick = onSettingsClick) {
                        Text(
                            text = "\u2699",
                            style = MaterialTheme.typography.titleLarge,
                            color = scheme.onSurfaceVariant,
                        )
                    }
                }
                Row(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier.width(220.dp).fillMaxHeight()
                            .background(scheme.surfaceVariant.copy(alpha = 0.3f))
                            .padding(vertical = 8.dp),
                    ) {
                        navItems.forEach { item ->
                            val isSelected = selectedPage == item.page
                            Row(
                                modifier = Modifier.fillMaxWidth()
                                    .clickable { selectedPage = item.page }
                                    .background(
                                        if (isSelected) scheme.primaryContainer.copy(alpha = 0.4f)
                                        else scheme.surface.copy(alpha = 0f)
                                    )
                                    .padding(horizontal = 20.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                if (item.icon != null) {
                                    Text(
                                        text = item.icon,
                                        style = MaterialTheme.typography.bodyLarge,
                                    )
                                    Spacer(Modifier.width(12.dp))
                                }
                                Text(
                                    text = item.label,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = if (isSelected) scheme.primary else scheme.onSurface,
                                )
                            }
                        }
                    }
                    Box(modifier = Modifier.fillMaxSize()) {
                        when (selectedPage) {
                            SidebarPage.OVERVIEW -> OverviewPage(projectInfo = projectInfo)
                            SidebarPage.MANIFEST -> ManifestPage()
                            SidebarPage.SOURCE_CODE -> SourceCodePage()
                        }
                    }
                }
            }
        }
    }
}
