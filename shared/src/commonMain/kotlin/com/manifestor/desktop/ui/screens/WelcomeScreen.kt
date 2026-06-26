package com.manifestor.desktop.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.manifestor.desktop.AppIcons
import com.manifestor.desktop.ProjectSummary
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
    }
}

@Composable
private fun ProjectItem(
    project: ProjectSummary,
    onClick: () -> Unit,
) {
    val scheme = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(scheme.surfaceVariant, RoundedCornerShape(12.dp))
            .padding(16.dp),
    ) {
        Column {
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
    }
}
