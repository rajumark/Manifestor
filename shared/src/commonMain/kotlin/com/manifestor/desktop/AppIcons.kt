package com.manifestor.desktop

import manifestor.shared.generated.resources.Res
import manifestor.shared.generated.resources.icon_manifest
import manifestor.shared.generated.resources.icon_menu
import manifestor.shared.generated.resources.icon_overview
import manifestor.shared.generated.resources.icon_settings
import manifestor.shared.generated.resources.icon_arrow_back
import manifestor.shared.generated.resources.icon_folder
import manifestor.shared.generated.resources.icon_manifest
import manifestor.shared.generated.resources.icon_menu
import manifestor.shared.generated.resources.icon_overview
import manifestor.shared.generated.resources.icon_search
import manifestor.shared.generated.resources.icon_settings
import manifestor.shared.generated.resources.icon_source
import org.jetbrains.compose.resources.DrawableResource

public object AppIcons {
    val arrowBack: DrawableResource get() = Res.drawable.icon_arrow_back
    val folder: DrawableResource get() = Res.drawable.icon_folder
    val search: DrawableResource get() = Res.drawable.icon_search
    val settings: DrawableResource get() = Res.drawable.icon_settings
    val menu: DrawableResource get() = Res.drawable.icon_menu
    val overview: DrawableResource get() = Res.drawable.icon_overview
    val manifest: DrawableResource get() = Res.drawable.icon_manifest
    val source: DrawableResource get() = Res.drawable.icon_source
}
