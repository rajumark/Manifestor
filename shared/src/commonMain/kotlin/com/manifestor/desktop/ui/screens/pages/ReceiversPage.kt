package com.manifestor.desktop.ui.screens.pages

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.manifestor.desktop.ApkOverviewData

@Composable
fun ReceiversPage(
    overviewData: ApkOverviewData = ApkOverviewData(),
    modifier: Modifier = Modifier,
) {
    val pkg = overviewData.appIdentity.packageName
    val sorted = remember(overviewData.manifestCategories.receivers, pkg) {
        val owned = overviewData.manifestCategories.receivers.filter { it.name.startsWith(pkg) }
        val other = overviewData.manifestCategories.receivers.filter { !it.name.startsWith(pkg) }
        owned + other
    }

    ComponentListPage(
        title = "Receivers",
        total = overviewData.manifestCategories.receivers.size,
        items = sorted,
        modifier = modifier,
    )
}
