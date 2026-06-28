package com.manifestor.desktop.ui.screens.pages

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.manifestor.desktop.ApkOverviewData

@Composable
fun PermissionsPage(
    overviewData: ApkOverviewData = ApkOverviewData(),
    modifier: Modifier = Modifier,
) {
    val scheme = MaterialTheme.colorScheme
    var searchQuery by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    val filtered = remember(overviewData.manifestCategories.permissions, searchQuery) {
        if (searchQuery.isBlank()) overviewData.manifestCategories.permissions
        else overviewData.manifestCategories.permissions.filter {
            it.name.contains(searchQuery, ignoreCase = true)
        }
    }

    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
    ) {
        Text(
            text = "Permissions",
            style = MaterialTheme.typography.titleLarge,
            color = scheme.onBackground,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 4.dp),
        )
        Text(
            text = "${overviewData.manifestCategories.permissions.size} total",
            style = MaterialTheme.typography.bodySmall,
            color = scheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 12.dp),
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search permissions...", fontSize = 13.sp) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {}),
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            textStyle = MaterialTheme.typography.bodySmall,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = scheme.primary,
                unfocusedBorderColor = scheme.outlineVariant,
            ),
        )

        Column(
            modifier = Modifier.fillMaxSize().verticalScroll(scrollState),
        ) {
            filtered.forEach { perm ->
                val isDangerous = perm.isDangerous
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = if (isDangerous) "⚠ " else "  ",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isDangerous) scheme.error else scheme.onSurfaceVariant,
                    )
                    Text(
                        text = perm.name,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isDangerous) scheme.error else scheme.onBackground,
                        fontSize = 12.sp,
                    )
                }
            }
        }
    }
}
