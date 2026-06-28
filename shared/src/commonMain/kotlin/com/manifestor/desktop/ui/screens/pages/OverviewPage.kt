package com.manifestor.desktop.ui.screens.pages

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.manifestor.desktop.ApkOverviewData
import com.manifestor.desktop.ProjectInfo
import org.jetbrains.skia.Image

@Composable
fun OverviewPage(
    projectInfo: ProjectInfo,
    overviewData: ApkOverviewData = ApkOverviewData(),
    modifier: Modifier = Modifier,
) {
    val scheme = MaterialTheme.colorScheme
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier.fillMaxSize().verticalScroll(scrollState),
    ) {
        Row(
            modifier = Modifier.padding(start = 24.dp, top = 24.dp, end = 24.dp, bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (overviewData.appIconBytes != null) {
                val bitmap = remember(overviewData.appIconBytes) {
                    Image.makeFromEncoded(overviewData.appIconBytes).toComposeImageBitmap()
                }
                Image(
                    painter = BitmapPainter(bitmap),
                    contentDescription = "App icon",
                    modifier = Modifier.size(48.dp).clip(RoundedCornerShape(10.dp)),
                )
                Spacer(Modifier.width(16.dp))
            }
            Column {
                if (overviewData.appName.isNotEmpty()) {
                    Text(
                        text = overviewData.appName,
                        style = MaterialTheme.typography.titleMedium,
                        color = scheme.onBackground,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Text(
                    text = overviewData.appIdentity.packageName,
                    style = MaterialTheme.typography.bodySmall,
                    color = scheme.onSurfaceVariant,
                )
            }
        }

        SectionCard {
            SectionTitle("App Identity", scheme.primary)
            KeyValue("Package", overviewData.appIdentity.packageName)
            KeyValue("Version", overviewData.appIdentity.versionName)
            KeyValue("Version Code", overviewData.appIdentity.versionCode)
            KeyValue("Min SDK", overviewData.appIdentity.minSdk)
            KeyValue("Target SDK", overviewData.appIdentity.targetSdk)
            KeyValue("Build Flavor", overviewData.appIdentity.buildFlavor)
        }

        SectionCard {
            SectionTitle("Manifest", scheme.primary)
            ManifestCategoryRow("Permissions", overviewData.manifestCategories.permissions)
            ManifestCategoryRow("Activities", overviewData.manifestCategories.activities)
            ManifestCategoryRow("Services", overviewData.manifestCategories.services)
            ManifestCategoryRow("Receivers", overviewData.manifestCategories.receivers)
            ManifestCategoryRow("Providers", overviewData.manifestCategories.providers)
            ManifestCategoryRow("Uses-feature", overviewData.manifestCategories.usesFeature.size)
            ManifestCategoryRow("Queries", overviewData.manifestCategories.queries.size)
            ManifestCategoryRow("Intent Filters", overviewData.manifestCategories.intentFilterSchemes.size)
        }

        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun SectionCard(
    content: @Composable ColumnScope.() -> Unit,
) {
    val scheme = MaterialTheme.colorScheme
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(scheme.surfaceVariant.copy(alpha = 0.3f))
            .border(0.5.dp, scheme.onSurfaceVariant.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
            .padding(16.dp),
        content = content,
    )
}

@Composable
private fun SectionTitle(text: String, primary: androidx.compose.ui.graphics.Color) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        color = primary,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(bottom = 8.dp),
    )
}

@Composable
private fun KeyValue(key: String, value: String) {
    val scheme = MaterialTheme.colorScheme
    if (value.isNotEmpty()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        ) {
            Text(
                text = "$key: ",
                style = MaterialTheme.typography.bodySmall,
                color = scheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodySmall,
                color = scheme.onBackground,
            )
        }
    }
}

@Composable
private fun ManifestCategoryRow(name: String, value: Any) {
    val scheme = MaterialTheme.colorScheme
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.bodySmall,
            color = scheme.onSurfaceVariant,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.bodySmall,
            color = scheme.onBackground,
            fontWeight = FontWeight.Medium,
        )
    }
}
