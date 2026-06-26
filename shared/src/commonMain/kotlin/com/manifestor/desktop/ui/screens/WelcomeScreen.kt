package com.manifestor.desktop.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.manifestor.desktop.ui.theme.AppColors

@Composable
fun WelcomeScreen(
    modifier: Modifier = Modifier,
    onBrowseClick: () -> Unit = {},
    onRecentClick: (String) -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppColors.background)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.weight(0.8f))

        Text(
            text = "Manifestor",
            style = androidx.compose.material3.Typography().displayLarge,
            color = AppColors.textPrimary,
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Reverse Engineer Any Android App",
            style = androidx.compose.material3.Typography().bodyLarge,
            color = AppColors.textSecondary,
        )

        Spacer(Modifier.height(40.dp))

        Box(
            modifier = Modifier
                .width(400.dp)
                .height(200.dp)
                .border(
                    width = 2.dp,
                    color = AppColors.divider,
                    shape = RoundedCornerShape(16.dp)
                )
                .background(AppColors.surface, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Drop APK file here",
                    style = androidx.compose.material3.Typography().titleMedium,
                    color = AppColors.textSecondary,
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "or",
                    style = androidx.compose.material3.Typography().bodyMedium,
                    color = AppColors.textSecondary,
                )
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = onBrowseClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.accent,
                        contentColor = AppColors.textPrimary,
                    ),
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Text("Browse File")
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        Text(
            text = "Supported: .apk files up to 500MB",
            style = androidx.compose.material3.Typography().labelMedium,
            color = AppColors.textSecondary,
        )

        Spacer(Modifier.weight(0.6f))

        HorizontalDivider(color = AppColors.divider, thickness = 1.dp)

        Spacer(Modifier.height(24.dp))

        Text(
            text = "Recent",
            style = androidx.compose.material3.Typography().titleMedium,
            color = AppColors.textPrimary,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(12.dp))

        RecentItem("com.instagram.android", "2 days ago", onClick = { onRecentClick("instagram") })
        RecentItem("com.spotify.music", "5 days ago", onClick = { onRecentClick("spotify") })

        Spacer(Modifier.weight(1f))
    }
}

@Composable
private fun RecentItem(packageName: String, timeAgo: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(AppColors.surface, RoundedCornerShape(8.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                text = packageName,
                style = androidx.compose.material3.Typography().bodyLarge,
                color = AppColors.textPrimary,
            )
            Text(
                text = timeAgo,
                style = androidx.compose.material3.Typography().labelMedium,
                color = AppColors.textSecondary,
            )
        }
        Text(
            text = "Open",
            style = androidx.compose.material3.Typography().labelMedium,
            color = AppColors.accent,
        )
    }
    Spacer(Modifier.height(8.dp))
}
