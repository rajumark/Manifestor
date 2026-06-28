package com.manifestor.desktop.ui.screens.pages

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.manifestor.desktop.AppIcons
import com.manifestor.desktop.FileEntry
import org.jetbrains.compose.resources.painterResource

@Composable
fun SourceCodePage(
    entries: List<FileEntry>,
    currentPath: String,
    canGoBack: Boolean,
    selectedFile: String?,
    searchResults: List<FileEntry>? = null,
    fileContent: String,
    onNavigate: (String) -> Unit,
    onBack: () -> Unit,
    onFileClick: (String) -> Unit,
    onDownload: () -> Unit = {},
    onCopy: () -> Unit = {},
    onSearchFile: (String) -> Unit = {},
    onSearchContent: (String) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val scheme = MaterialTheme.colorScheme
    val scrollState = rememberScrollState()
    var showFileSearch by remember { mutableStateOf(false) }
    var showContentSearch by remember { mutableStateOf(false) }
    var fileSearchQuery by remember { mutableStateOf("") }
    var contentSearchQuery by remember { mutableStateOf("") }

    val displayEntries = searchResults ?: if (fileSearchQuery.isEmpty()) entries
    else entries.filter { it.name.contains(fileSearchQuery, ignoreCase = true) }

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
                    onClick = { showFileSearch = !showFileSearch },
                    modifier = Modifier.size(32.dp),
                ) {
                    Icon(
                        painter = painterResource(AppIcons.search),
                        contentDescription = "Search files",
                        modifier = Modifier.size(14.dp),
                        tint = if (showFileSearch) scheme.primary else scheme.onSurfaceVariant,
                    )
                }
            }

            if (showFileSearch) {
                Row(
                    modifier = Modifier.fillMaxWidth().height(28.dp)
                        .background(scheme.surfaceVariant)
                        .padding(horizontal = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    BasicTextField(
                        value = fileSearchQuery,
                        onValueChange = { fileSearchQuery = it; onSearchFile(it) },
                        singleLine = true,
                        textStyle = TextStyle(
                            color = scheme.onSurface,
                            fontSize = 12.sp,
                        ),
                        cursorBrush = SolidColor(scheme.primary),
                        modifier = Modifier.weight(1f),
                        decorationBox = { innerTextField ->
                            Box {
                                if (fileSearchQuery.isEmpty()) {
                                    Text(
                                        "Search files...",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = scheme.onSurfaceVariant,
                                    )
                                }
                                innerTextField()
                            }
                        },
                    )
                    if (fileSearchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = { fileSearchQuery = ""; onSearchFile("") },
                            modifier = Modifier.size(24.dp),
                        ) {
                            Icon(
                                painter = painterResource(AppIcons.close),
                                contentDescription = "Clear",
                                modifier = Modifier.size(12.dp),
                                tint = scheme.onSurfaceVariant,
                            )
                        }
                    }
                }
            }

            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                Column(
                    modifier = Modifier.fillMaxSize()
                        .verticalScroll(scrollState),
                ) {
                    displayEntries.forEach { entry ->
                        val isSelected = selectedFile == entry.relativePath
                        val showPath = searchResults != null && entry.relativePath.contains("/")
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .defaultMinSize(minHeight = 28.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .clickable {
                                    if (entry.isDirectory) onNavigate(entry.relativePath)
                                    else onFileClick(entry.relativePath)
                                }
                                .background(
                                    if (isSelected) scheme.primaryContainer.copy(alpha = 0.4f)
                                    else scheme.surface.copy(alpha = 0f)
                                )
                                .padding(horizontal = 8.dp, vertical = if (showPath) 4.dp else 0.dp),
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
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = entry.name,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = scheme.onSurface,
                                    maxLines = 1,
                                )
                                if (searchResults != null && entry.relativePath.contains("/")) {
                                    Text(
                                        text = entry.relativePath.substringBeforeLast("/"),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = scheme.onSurfaceVariant.copy(alpha = 0.6f),
                                        fontSize = 10.sp,
                                        maxLines = 1,
                                    )
                                }
                            }
                        }
                    }
                }

                VerticalScrollbar(
                    modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                    adapter = rememberScrollbarAdapter(scrollState = scrollState),
                )
            }
        }

        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth().height(32.dp)
                    .background(scheme.surface),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (showContentSearch) {
                    Row(
                        modifier = Modifier.weight(1f).height(28.dp)
                            .background(scheme.surfaceVariant)
                            .padding(horizontal = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        BasicTextField(
                            value = contentSearchQuery,
                            onValueChange = { contentSearchQuery = it; onSearchContent(it) },
                            singleLine = true,
                            textStyle = TextStyle(
                                color = scheme.onSurface,
                                fontSize = 12.sp,
                            ),
                            cursorBrush = SolidColor(scheme.primary),
                            modifier = Modifier.weight(1f),
                            decorationBox = { innerTextField ->
                                Box {
                                    if (contentSearchQuery.isEmpty()) {
                                        Text(
                                            "Search content...",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = scheme.onSurfaceVariant,
                                        )
                                    }
                                    innerTextField()
                                }
                            },
                        )
                        if (contentSearchQuery.isNotEmpty()) {
                            IconButton(
                                onClick = { contentSearchQuery = ""; onSearchContent("") },
                                modifier = Modifier.size(24.dp),
                            ) {
                                Icon(
                                    painter = painterResource(AppIcons.close),
                                    contentDescription = "Clear",
                                    modifier = Modifier.size(12.dp),
                                    tint = scheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                } else {
                    Spacer(Modifier.weight(1f))
                }
                IconButton(
                    onClick = { showContentSearch = !showContentSearch },
                    modifier = Modifier.size(32.dp),
                ) {
                    Icon(
                        painter = painterResource(AppIcons.search),
                        contentDescription = "Search content",
                        modifier = Modifier.size(14.dp),
                        tint = if (showContentSearch) scheme.primary else scheme.onSurfaceVariant,
                    )
                }
                IconButton(
                    onClick = onDownload,
                    modifier = Modifier.size(32.dp),
                ) {
                    Icon(
                        painter = painterResource(AppIcons.download),
                        contentDescription = "Download",
                        modifier = Modifier.size(14.dp),
                        tint = scheme.onSurfaceVariant,
                    )
                }
                IconButton(
                    onClick = onCopy,
                    modifier = Modifier.size(32.dp),
                ) {
                    Icon(
                        painter = painterResource(AppIcons.copy),
                        contentDescription = "Copy",
                        modifier = Modifier.size(14.dp),
                        tint = scheme.onSurfaceVariant,
                    )
                }
            }

            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                if (selectedFile != null && fileContent.isNotEmpty()) {
                    val contentScrollState = rememberScrollState()
                    Column(
                        modifier = Modifier.fillMaxSize()
                            .verticalScroll(contentScrollState)
                            .padding(16.dp),
                    ) {
                        Text(
                            text = fileContent,
                            style = MaterialTheme.typography.bodySmall,
                            color = scheme.onSurface,
                        )
                    }

                    VerticalScrollbar(
                        modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                        adapter = rememberScrollbarAdapter(scrollState = contentScrollState),
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = if (selectedFile != null) "Loading..." else "Select a file",
                            style = MaterialTheme.typography.bodySmall,
                            color = scheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}
