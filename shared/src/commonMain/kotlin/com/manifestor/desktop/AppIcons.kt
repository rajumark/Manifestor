@file:OptIn(org.jetbrains.compose.resources.InternalResourceApi::class)

package com.manifestor.desktop

import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.ResourceItem

private val base = "composeResources/manifestor.shared.generated.resources/"

private fun rsc(name: String): DrawableResource = DrawableResource(
    "drawable:$name",
    setOf(ResourceItem(setOf(), "${base}drawable/$name.svg", -1, -1)),
)

public object AppIcons {
    val arrowBack: DrawableResource get() = rsc("icon_arrow_back")
    val close: DrawableResource get() = rsc("icon_close")
    val copy: DrawableResource get() = rsc("icon_copy")
    val download: DrawableResource get() = rsc("icon_download")
    val folder: DrawableResource get() = rsc("icon_folder")
    val search: DrawableResource get() = rsc("icon_search")
    val settings: DrawableResource get() = rsc("icon_settings")
    val menu: DrawableResource get() = rsc("icon_menu")
    val overview: DrawableResource get() = rsc("icon_overview")
    val manifest: DrawableResource get() = rsc("icon_manifest")
    val permissions: DrawableResource get() = rsc("icon_permissions")
    val activities: DrawableResource get() = rsc("icon_activities")
    val services: DrawableResource get() = rsc("icon_services")
    val receivers: DrawableResource get() = rsc("icon_receivers")
    val providers: DrawableResource get() = rsc("icon_providers")
    val usesFeature: DrawableResource get() = rsc("icon_uses_feature")
    val queries: DrawableResource get() = rsc("icon_queries")
    val intentFilters: DrawableResource get() = rsc("icon_intent_filters")
    val source: DrawableResource get() = rsc("icon_source")
}
