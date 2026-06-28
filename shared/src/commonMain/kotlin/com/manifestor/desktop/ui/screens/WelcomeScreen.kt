package com.manifestor.desktop.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.manifestor.desktop.AppIcons
import com.manifestor.desktop.ProjectSummary
import com.manifestor.desktop.RecentApk
import org.jetbrains.compose.resources.painterResource

@Composable
fun WelcomeScreen(
    modifier: Modifier = Modifier,
    apkPath: String? = null,
    isDragging: Boolean = false,
    projectName: String = "",
    onProjectNameChange: (String) -> Unit = {},
    onCreateProject: () -> Unit = {},
    errorMessage: String? = null,
    onSettingsClick: () -> Unit = {},
    onBrowseClick: () -> Unit = {},
    onClearApk: () -> Unit = {},
    projects: List<ProjectSummary> = emptyList(),
    onProjectClick: (ProjectSummary) -> Unit = {},
    onProjectDelete: (ProjectSummary) -> Unit = {},
    onProjectOpenFolder: (ProjectSummary) -> Unit = {},
    onProjectCopyPath: (ProjectSummary) -> Unit = {},
    recentApks: List<RecentApk> = emptyList(),
    onRecentApkClick: (String) -> Unit = {},
    onRecentApksRefresh: () -> Unit = {},
    showBackButton: Boolean = false,
    onBackClick: () -> Unit = {},
) {
    val scheme = MaterialTheme.colorScheme

    val borderColor by animateColorAsState(
        if (isDragging) scheme.primary else scheme.outlineVariant,
        label = "borderColor",
    )
    val bgColor by animateColorAsState(
        if (isDragging) scheme.primary.copy(alpha = 0.08f) else scheme.surface,
        label = "bgColor",
    )
    var showRecentApksDialog by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(scheme.background),
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(start = 32.dp, end = 32.dp),
        ) {
            Column(
                modifier = Modifier.weight(1f).fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = "Manifestor",
                        style = MaterialTheme.typography.displayLarge,
                        color = scheme.onSurface,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Reverse Engineer Any Android App",
                        style = MaterialTheme.typography.bodyLarge,
                        color = scheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(40.dp))
                    Box(
                        modifier = Modifier
                            .width(400.dp)
                            .border(
                                width = if (isDragging) 2.dp else 1.dp,
                                color = borderColor,
                                shape = RoundedCornerShape(16.dp),
                            )
                            .background(bgColor, RoundedCornerShape(16.dp))
                            .padding(24.dp),
                    ) {
                        if (apkPath != null) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Text(
                                    text = "APK Selected",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = scheme.tertiary,
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = apkPath.substringAfterLast("/"),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = scheme.onSurface,
                                )
                                Spacer(Modifier.height(16.dp))
                                OutlinedTextField(
                                    value = projectName,
                                    onValueChange = { input ->
                                        val filtered = input.filter { c -> c.isLetterOrDigit() || c == '_' }
                                        onProjectNameChange(filtered)
                                    },
                                    label = { Text("Project Name") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                )
                                if (errorMessage != null) {
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        text = errorMessage,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = scheme.error,
                                    )
                                }
                                Spacer(Modifier.height(16.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End,
                                ) {
                                    TextButton(onClick = onClearApk) {
                                        Text("Remove")
                                    }
                                    Spacer(Modifier.width(8.dp))
                                    Button(onClick = onCreateProject) {
                                        Text("Create Project")
                                    }
                                }
                            }
                        } else {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                Spacer(Modifier.height(24.dp))
                                Text(
                                    text = if (isDragging) "Drop APK here" else "Drop APK file here",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = if (isDragging) scheme.primary else scheme.onSurfaceVariant,
                                )
                                Spacer(Modifier.height(12.dp))
                                if (!isDragging) {
                                    Text(
                                        text = "or",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = scheme.onSurfaceVariant,
                                    )
                                    Spacer(Modifier.height(12.dp))
                                    Button(onClick = onBrowseClick) {
                                        Text("Browse File")
                                    }
                                }
                                Spacer(Modifier.height(24.dp))
                            }
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = "Supported: .apk files up to 500MB",
                        style = MaterialTheme.typography.labelMedium,
                        color = scheme.onSurfaceVariant,
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.Start,
                ) {
                    TextButton(
                        onClick = {
                            onRecentApksRefresh()
                            showRecentApksDialog = true
                        },
                    ) {
                        Text("Recent APKs")
                    }
                }
            }

            if (projects.isNotEmpty()) {
                Column(
                    modifier = Modifier.width(320.dp).fillMaxHeight().padding(start = 32.dp, top = 48.dp, bottom = 48.dp),
                    horizontalAlignment = Alignment.Start,
                ) {
                    Text(
                        text = "Recent Projects",
                        style = MaterialTheme.typography.titleMedium,
                        color = scheme.onSurface,
                    )
                    Spacer(Modifier.height(12.dp))
                    Column(
                        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                    ) {
                        projects.forEach { project ->
                            ProjectItem(
                                project = project,
                                onClick = { onProjectClick(project) },
                                onDelete = { onProjectDelete(project) },
                                onOpenFolder = { onProjectOpenFolder(project) },
                                onCopyPath = { onProjectCopyPath(project) },
                            )
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                }
            }
        }

        if (showBackButton) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.align(Alignment.TopStart).padding(24.dp),
            ) {
                Icon(
                    painter = painterResource(AppIcons.arrowBack),
                    contentDescription = "Back",
                    tint = scheme.onSurfaceVariant,
                )
            }
        }

        IconButton(
            onClick = onSettingsClick,
            modifier = Modifier.align(Alignment.TopEnd).padding(24.dp),
        ) {
            Icon(
                painter = painterResource(AppIcons.settings),
                contentDescription = "Settings",
                tint = scheme.onSurfaceVariant,
            )
        }

        if (showRecentApksDialog) {
            RecentApksDialog(
                apks = recentApks,
                onApkClick = { path ->
                    onRecentApkClick(path)
                    showRecentApksDialog = false
                },
                onDismiss = { showRecentApksDialog = false },
                onRefresh = {
                    onRecentApksRefresh()
                },
            )
        }
    }
}

@Composable
private fun RecentApksDialog(
    apks: List<RecentApk>,
    onApkClick: (String) -> Unit,
    onDismiss: () -> Unit,
    onRefresh: () -> Unit,
) {
    val scheme = MaterialTheme.colorScheme

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("Recent APKs")
                IconButton(onClick = onRefresh) {
                    Text("↻", style = MaterialTheme.typography.titleMedium)
                }
            }
        },
        text = {
            if (apks.isEmpty()) {
                Text(
                    text = "No APK files found in Downloads",
                    style = MaterialTheme.typography.bodyMedium,
                    color = scheme.onSurfaceVariant,
                )
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp)
                        .verticalScroll(rememberScrollState()),
                ) {
                    apks.forEach { apk ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onApkClick(apk.filePath) }
                                .background(scheme.surfaceVariant, RoundedCornerShape(8.dp))
                                .padding(12.dp),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = apk.fileName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = scheme.onSurface,
                                )
                                Text(
                                    text = apk.relativeTime,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = scheme.onSurfaceVariant,
                                )
                            }
                        }
                        Spacer(Modifier.height(6.dp))
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        },
    )
}

@Composable
private fun ProjectItem(
    project: ProjectSummary,
    onClick: () -> Unit,
    onDelete: () -> Unit = {},
    onOpenFolder: () -> Unit = {},
    onCopyPath: () -> Unit = {},
) {
    val scheme = MaterialTheme.colorScheme
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    var showMenu by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .hoverable(interactionSource)
            .clickable(onClick = onClick)
            .background(scheme.surfaceVariant, RoundedCornerShape(12.dp))
            .padding(start = 16.dp, top = 12.dp, bottom = 12.dp, end = 4.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = project.projectName,
                    style = MaterialTheme.typography.titleSmall,
                    color = scheme.onSurface,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = project.apkFileName,
                    style = MaterialTheme.typography.bodySmall,
                    color = scheme.onSurfaceVariant,
                )
                Text(
                    text = "Created ${project.createdAtDisplay}",
                    style = MaterialTheme.typography.labelSmall,
                    color = scheme.onSurfaceVariant,
                )
            }

            if (isHovered || showMenu) {
                Box {
                    IconButton(onClick = { showMenu = true }) {
                        Text(
                            text = "\u22EE",
                            style = MaterialTheme.typography.titleMedium,
                            color = scheme.onSurfaceVariant,
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                    ) {
                        DropdownMenuItem(
                            text = { Text("Open Folder") },
                            onClick = { showMenu = false; onOpenFolder() },
                        )
                        DropdownMenuItem(
                            text = { Text("Copy Path") },
                            onClick = { showMenu = false; onCopyPath() },
                        )
                        DropdownMenuItem(
                            text = { Text("Delete Project") },
                            onClick = { showMenu = false; onDelete() },
                        )
                    }
                }
            }
        }
    }
}
