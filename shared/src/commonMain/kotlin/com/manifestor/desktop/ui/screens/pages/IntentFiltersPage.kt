package com.manifestor.desktop.ui.screens.pages

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.manifestor.desktop.ApkOverviewData

@Composable
fun IntentFiltersPage(
    overviewData: ApkOverviewData = ApkOverviewData(),
    modifier: Modifier = Modifier,
) {
    val scheme = MaterialTheme.colorScheme
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier.fillMaxSize().verticalScroll(scrollState).padding(24.dp),
    ) {
        Text(
            text = "Intent Filters",
            style = MaterialTheme.typography.titleLarge,
            color = scheme.onBackground,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 4.dp),
        )
        Text(
            text = "${overviewData.manifestCategories.intentFilterSchemes.size} total",
            style = MaterialTheme.typography.bodySmall,
            color = scheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        overviewData.manifestCategories.intentFilterSchemes.forEach { s ->
            Text(
                text = s,
                style = MaterialTheme.typography.bodySmall,
                color = scheme.onBackground,
                fontSize = 12.sp,
                modifier = Modifier.padding(vertical = 3.dp),
            )
        }
    }
}
