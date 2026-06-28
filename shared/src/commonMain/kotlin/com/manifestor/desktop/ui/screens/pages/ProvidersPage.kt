package com.manifestor.desktop.ui.screens.pages

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.manifestor.desktop.ApkOverviewData
import com.manifestor.desktop.ManifestProvider

@Composable
fun ProvidersPage(
    overviewData: ApkOverviewData = ApkOverviewData(),
    modifier: Modifier = Modifier,
) {
    val scheme = MaterialTheme.colorScheme
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
    ) {
        Text(
            text = "Providers",
            style = MaterialTheme.typography.titleLarge,
            color = scheme.onBackground,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 4.dp),
        )
        Text(
            text = "${overviewData.manifestCategories.providers.size} total",
            style = MaterialTheme.typography.bodySmall,
            color = scheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 12.dp),
        )

        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(scrollState),
        ) {
            overviewData.manifestCategories.providers.forEach { prv ->
                ProviderItem(prv)
                if (prv != overviewData.manifestCategories.providers.last()) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 2.dp),
                        color = scheme.outlineVariant.copy(alpha = 0.4f),
                    )
                }
            }
        }
    }
}

@Composable
private fun ProviderItem(prv: ManifestProvider) {
    val scheme = MaterialTheme.colorScheme
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(vertical = 6.dp, horizontal = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = if (expanded) "▼ " else "▶ ",
                style = MaterialTheme.typography.bodySmall,
                color = scheme.onSurfaceVariant,
                fontSize = 10.sp,
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = prv.name,
                style = MaterialTheme.typography.bodySmall,
                color = scheme.onBackground,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f),
            )
            prv.exported?.let {
                Text(
                    text = if (it) "exported" else "not exported",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (it) scheme.tertiary else scheme.onSurfaceVariant,
                    fontSize = 10.sp,
                )
            }
        }

        AnimatedVisibility(visible = expanded) {
            Column(
                modifier = Modifier.fillMaxWidth()
                    .padding(start = 26.dp, bottom = 8.dp),
            ) {
                if (prv.authorities.isNotEmpty()) {
                    DetailRow("authorities", prv.authorities)
                }
                if (prv.readPermission.isNotEmpty()) {
                    DetailRow("readPermission", prv.readPermission)
                }
                if (prv.writePermission.isNotEmpty()) {
                    DetailRow("writePermission", prv.writePermission)
                }
                prv.grantUriPermissions?.let {
                    DetailRow("grantUriPermissions", it.toString())
                }
            }
        }
    }
}
