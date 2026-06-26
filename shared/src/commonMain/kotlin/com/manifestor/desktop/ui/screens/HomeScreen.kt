package com.manifestor.desktop.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.manifestor.desktop.AppIcons
import com.manifestor.desktop.ProjectInfo
import com.manifestor.desktop.ui.screens.pages.ManifestPage
import com.manifestor.desktop.ui.screens.pages.OverviewPage
import com.manifestor.desktop.ui.screens.pages.SourceCodePage
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

enum class SidebarPage { OVERVIEW, MANIFEST, SOURCE_CODE }

private data class NavItem(
    val page: SidebarPage,
    val icon: DrawableResource?,
    val label: String,
)

private val navItems = listOf(
    NavItem(SidebarPage.OVERVIEW, AppIcons.overview, "Overview"),
    NavItem(SidebarPage.MANIFEST, AppIcons.manifest, "Manifest"),
    NavItem(SidebarPage.SOURCE_CODE, AppIcons.source, "Source Code"),
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
    var showSidebar by remember { mutableStateOf(true) }

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
                    modifier = Modifier.fillMaxWidth().height(32.dp).background(scheme.surface),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = { showSidebar = !showSidebar }) {
                        Icon(
                            painter = painterResource(AppIcons.menu),
                            contentDescription = "Menu",
                            modifier = Modifier.size(18.dp),
                            tint = scheme.onSurface,
                        )
                    }
                    Text(
                        text = projectInfo.projectName,
                        style = MaterialTheme.typography.titleSmall,
                        color = scheme.onSurface,
                        modifier = Modifier.weight(1f),
                    )
                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            painter = painterResource(AppIcons.settings),
                            contentDescription = "Settings",
                            modifier = Modifier.size(18.dp),
                            tint = scheme.onSurfaceVariant,
                        )
                    }
                }
                Row(modifier = Modifier.fillMaxSize()) {
                    if (showSidebar) {
                        Column(
                            modifier = Modifier.width(160.dp).fillMaxHeight()
                                .padding(vertical = 8.dp),
                        ) {
                            navItems.forEach { item ->
                                val isSelected = selectedPage == item.page
                                val itemShape = RoundedCornerShape(
                                    topStart = 0.dp, topEnd = 16.dp,
                                    bottomEnd = 16.dp, bottomStart = 0.dp,
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth()
                                        .clip(itemShape)
                                        .clickable { selectedPage = item.page }
                                        .background(
                                            if (isSelected) scheme.primaryContainer.copy(alpha = 0.4f)
                                            else scheme.surface.copy(alpha = 0f),
                                            itemShape,
                                        )
                                        .height(32.dp)
                                        .padding(horizontal = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    if (item.icon != null) {
                                        Icon(
                                            painter = painterResource(item.icon),
                                            contentDescription = item.label,
                                            modifier = Modifier.size(14.dp),
                                            tint = if (isSelected) scheme.primary else scheme.onSurfaceVariant,
                                        )
                                        Spacer(Modifier.width(12.dp))
                                    }
                                    Text(
                                        text = item.label,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (isSelected) scheme.primary else scheme.onSurface,
                                    )
                                }
                            }
                        }
                    }
                    Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
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
