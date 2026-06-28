package com.manifestor.desktop.ui.screens.pages

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.manifestor.desktop.ApkOverviewData

@Composable
fun ActivitiesPage(
    overviewData: ApkOverviewData = ApkOverviewData(),
    modifier: Modifier = Modifier,
) {
    val pkg = overviewData.appIdentity.packageName
    val sorted = remember(overviewData.manifestCategories.activities, pkg) {
        val owned = overviewData.manifestCategories.activities.filter { it.name.startsWith(pkg) }
        val other = overviewData.manifestCategories.activities.filter { !it.name.startsWith(pkg) }
        owned + other
    }

    ComponentListPage(
        title = "Activities",
        total = overviewData.manifestCategories.activities.size,
        items = sorted,
        modifier = modifier,
    )
}
