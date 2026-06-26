package com.manifestor.desktop.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun WelcomeScreen(
    modifier: Modifier = Modifier,
    apkPath: String? = null,
    isDragging: Boolean = false,
    onBrowseClick: () -> Unit = {},
    onClearApk: () -> Unit = {},
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(scheme.background)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.weight(0.8f))

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
                .height(200.dp)
                .border(
                    width = if (isDragging) 2.dp else 1.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(16.dp),
                )
                .background(bgColor, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center,
        ) {
            if (apkPath != null) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "APK Selected",
                        style = MaterialTheme.typography.titleMedium,
                        color = scheme.tertiary,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = apkPath.substringAfterLast("/"),
                        style = MaterialTheme.typography.bodyMedium,
                        color = scheme.onSurface,
                    )
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = "Processing will be available in next step",
                        style = MaterialTheme.typography.labelMedium,
                        color = scheme.onSurfaceVariant,
                    )
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = onClearApk,
                    ) {
                        Text("Remove")
                    }
                }
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
                        Button(
                            onClick = onBrowseClick,
                        ) {
                            Text("Browse File")
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        Text(
            text = "Supported: .apk files up to 500MB",
            style = MaterialTheme.typography.labelMedium,
            color = scheme.onSurfaceVariant,
        )

        Spacer(Modifier.weight(1f))
    }
}
