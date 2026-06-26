package com.manifestor.desktop.ui.screens.pages

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.manifestor.desktop.AppIcons
import com.manifestor.desktop.FileEntry
import org.jetbrains.compose.resources.painterResource

@Composable
fun SourceCodePage(
    entries: List<FileEntry>,
    currentPath: String,
    canGoBack: Boolean,
    selectedFile: String?,
    onNavigate: (String) -> Unit,
    onBack: () -> Unit,
    onFileClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scheme = MaterialTheme.colorScheme
    val scrollState = rememberScrollState()

    Row(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.width(220.dp).fillMaxHeight()
                .background(scheme.surfaceVariant.copy(alpha = 0.15f)),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().height(32.dp)
                    .background(scheme.surface),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (currentPath.isNotEmpty()) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.size(32.dp),
                    ) {
                        Icon(
                            painter = painterResource(AppIcons.arrowBack),
                            contentDescription = "Back",
                            modifier = Modifier.size(14.dp),
                            tint = scheme.onSurface,
                        )
                    }
                }
                Spacer(Modifier.weight(1f))
                IconButton(
                    onClick = { },
                    modifier = Modifier.size(32.dp),
                ) {
                    Icon(
                        painter = painterResource(AppIcons.search),
                        contentDescription = "Search",
                        modifier = Modifier.size(14.dp),
                        tint = scheme.onSurfaceVariant,
                    )
                }
            }

            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                Column(
                    modifier = Modifier.fillMaxSize()
                        .verticalScroll(scrollState),
                ) {
                    entries.forEach { entry ->
                        val isSelected = selectedFile == entry.relativePath
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(28.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .clickable {
                                    if (entry.isDirectory) onNavigate(entry.relativePath)
                                    else onFileClick(entry.relativePath)
                                }
                                .background(
                                    if (isSelected) scheme.primaryContainer.copy(alpha = 0.4f)
                                    else scheme.surface.copy(alpha = 0f)
                                )
                                .padding(horizontal = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                painter = painterResource(
                                    if (entry.isDirectory) AppIcons.folder
                                    else AppIcons.manifest
                                ),
                                contentDescription = if (entry.isDirectory) "Folder" else "File",
                                modifier = Modifier.size(14.dp),
                                tint = if (entry.isDirectory) scheme.tertiary else scheme.onSurfaceVariant,
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = entry.name,
                                style = MaterialTheme.typography.bodySmall,
                                color = scheme.onSurface,
                                maxLines = 1,
                            )
                        }
                    }
                }

                VerticalScrollbar(
                    modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                    adapter = rememberScrollbarAdapter(scrollState = scrollState),
                )
            }
        }

        Box(
            modifier = Modifier.fillMaxSize().padding(16.dp),
        ) {
            if (selectedFile != null) {
                Text(
                    text = selectedFile,
                    style = MaterialTheme.typography.bodySmall,
                    color = scheme.onSurfaceVariant,
                )
            } else {
                Text(
                    text = "Select a file to view its path",
                    style = MaterialTheme.typography.bodySmall,
                    color = scheme.onSurfaceVariant,
                )
            }
        }
    }
}
