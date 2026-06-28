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
import com.manifestor.desktop.IntentFilter
import com.manifestor.desktop.ManifestComponent

@Composable
fun ComponentListPage(
    title: String,
    total: Int,
    items: List<ManifestComponent>,
    modifier: Modifier = Modifier,
) {
    val scheme = MaterialTheme.colorScheme
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = scheme.onBackground,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 4.dp),
        )
        Text(
            text = "$total total",
            style = MaterialTheme.typography.bodySmall,
            color = scheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 12.dp),
        )

        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(scrollState),
        ) {
            items.forEach { comp ->
                ComponentItem(comp)
                if (comp != items.last()) {
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
fun ComponentItem(comp: ManifestComponent) {
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
            Text(
                text = comp.name,
                style = MaterialTheme.typography.bodySmall,
                color = scheme.onBackground,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f),
            )
            comp.exported?.let {
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
                    .padding(start = 20.dp, bottom = 8.dp),
            ) {
                if (comp.launchMode.isNotEmpty()) {
                    DetailRow("launchMode", comp.launchMode)
                }
                if (comp.theme.isNotEmpty()) {
                    DetailRow("theme", comp.theme)
                }
                if (comp.process.isNotEmpty()) {
                    DetailRow("process", comp.process)
                }
                if (comp.orientation.isNotEmpty()) {
                    DetailRow("screenOrientation", comp.orientation)
                }
                if (comp.configChanges.isNotEmpty()) {
                    DetailRow("configChanges", comp.configChanges)
                }

                comp.intentFilters.forEachIndexed { idx, filter ->
                    IntentFilterCard(filter, idx)
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    val scheme = MaterialTheme.colorScheme
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 1.dp),
    ) {
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.bodySmall,
            color = scheme.onSurfaceVariant,
            fontSize = 10.sp,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            color = scheme.onBackground,
            fontSize = 10.sp,
        )
    }
}

@Composable
fun IntentFilterCard(filter: IntentFilter, index: Int) {
    val scheme = MaterialTheme.colorScheme
    var ifExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
            .border(1.dp, scheme.outlineVariant.copy(alpha = 0.3f), MaterialTheme.shapes.small)
            .clip(MaterialTheme.shapes.small),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .clickable { ifExpanded = !ifExpanded }
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "intent-filter #${index + 1}",
                style = MaterialTheme.typography.bodySmall,
                color = scheme.primary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f),
            )
            if (filter.autoVerify) {
                Text(
                    text = "autoVerify",
                    style = MaterialTheme.typography.bodySmall,
                    color = scheme.tertiary,
                    fontSize = 9.sp,
                )
            }
            Text(
                text = if (ifExpanded) "▲" else "▼",
                style = MaterialTheme.typography.bodySmall,
                color = scheme.onSurfaceVariant,
                fontSize = 9.sp,
            )
        }

        AnimatedVisibility(visible = ifExpanded) {
            Column(modifier = Modifier.padding(start = 8.dp, bottom = 4.dp, end = 8.dp)) {
                if (filter.actions.isNotEmpty()) {
                    Text(
                        text = "actions: ${filter.actions.joinToString(", ")}",
                        style = MaterialTheme.typography.bodySmall,
                        color = scheme.onBackground,
                        fontSize = 9.sp,
                    )
                }
                if (filter.categories.isNotEmpty()) {
                    Text(
                        text = "categories: ${filter.categories.joinToString(", ")}",
                        style = MaterialTheme.typography.bodySmall,
                        color = scheme.onBackground,
                        fontSize = 9.sp,
                    )
                }
                filter.dataSchemes.forEach { schemeVal ->
                    val host = filter.dataHosts.firstOrNull() ?: ""
                    val path = filter.dataPaths.firstOrNull() ?: ""
                    Text(
                        text = "data: $schemeVal${if (host.isNotEmpty()) "://$host" else ""}$path",
                        style = MaterialTheme.typography.bodySmall,
                        color = scheme.onBackground,
                        fontSize = 9.sp,
                    )
                }
                if (filter.dataMimeTypes.isNotEmpty()) {
                    Text(
                        text = "mimeType: ${filter.dataMimeTypes.joinToString(", ")}",
                        style = MaterialTheme.typography.bodySmall,
                        color = scheme.onBackground,
                        fontSize = 9.sp,
                    )
                }
            }
        }
    }
}
