package com.manifestor.desktop

import java.io.File

object ApkOverviewParser {

    fun parse(projectDir: String): ApkOverviewData {
        val jadxDir = File(projectDir, "jadx_result")
        if (!jadxDir.exists()) return ApkOverviewData()

        val manifestFile = File(jadxDir, "resources/AndroidManifest.xml")
        val sourcesDir = File(jadxDir, "sources")
        val resDir = File(jadxDir, "resources/res")

        val manifestText = if (manifestFile.exists()) manifestFile.readText() else ""

        return ApkOverviewData(
            appName = resolveAppName(manifestText, resDir),
            appIconBytes = resolveAppIcon(resDir),
            appIdentity = parseAppIdentity(manifestText, sourcesDir),
            manifestCategories = parseManifestCategories(manifestText),
        )
    }

    private fun parseAppIdentity(manifest: String, sourcesDir: File): AppIdentity {
        val pkg = extractAttr(manifest, "package")
        val versionName = extractAttr(manifest, "android:versionName")
        val versionCode = extractAttr(manifest, "android:versionCode")
        val minSdk = extractAttr(manifest, "android:minSdkVersion")
        val targetSdk = extractAttr(manifest, "android:targetSdkVersion")
        val flavor = findBuildConfigValue(sourcesDir, "FLAVOR") ?: ""

        return AppIdentity(
            packageName = pkg,
            versionName = versionName,
            versionCode = versionCode,
            minSdk = minSdk,
            targetSdk = targetSdk,
            buildFlavor = flavor,
        )
    }

    private val dangerousPermissions = listOf(
        "INSTALL_PACKAGES", "REQUEST_INSTALL_PACKAGES", "REQUEST_DELETE_PACKAGES",
        "READ_EXTERNAL_STORAGE", "WRITE_EXTERNAL_STORAGE", "MANAGE_EXTERNAL_STORAGE",
        "CAMERA", "RECORD_AUDIO", "ACCESS_FINE_LOCATION", "ACCESS_COARSE_LOCATION",
        "READ_CONTACTS", "WRITE_CONTACTS", "READ_CALL_LOG", "WRITE_CALL_LOG",
        "READ_SMS", "SEND_SMS", "RECEIVE_SMS",
        "QUERY_ALL_PACKAGES", "SYSTEM_ALERT_WINDOW",
        "BIND_ACCESSIBILITY_SERVICE",
    )

    private fun parseManifestCategories(manifest: String): ManifestCategories {
        val attrNameRegex = Regex("""android:name="([^"]*)"""")
        val exportedRegex = Regex("""android:exported="(true|false)"""")

        val permissions = Regex("<uses-permission[^>]*android:name=\"([^\"]*)\"").findAll(manifest).map {
            ManifestPermission(
                name = it.groupValues[1],
                isDangerous = dangerousPermissions.any { d -> it.groupValues[1].contains(d) },
            )
        }.toList()

        val activities = parseComponents(manifest, "activity")
        val services = parseComponents(manifest, "service")
        val receivers = parseComponents(manifest, "receiver")

        val providers = parseProviderBlocks(manifest)

        val usesFeature = Regex("<uses-feature[\\s\\S]*?</uses-feature>").findAll(manifest).map { block ->
            attrNameRegex.find(block.value)?.groupValues?.getOrNull(1) ?: ""
        }.filter { it.isNotEmpty() }.toList()

        val queries = Regex("<package[\\s\\S]*?android:name=\"([^\"]*)\"").findAll(manifest).map {
            it.groupValues[1]
        }.toList()

        val schemes = mutableListOf<String>()
        Regex("""android:scheme="([^"]*)"""").findAll(manifest).forEach { m ->
            val s = m.groupValues[1].removePrefix("@string/")
            if (s !in schemes) schemes.add(s)
        }

        return ManifestCategories(
            permissions = permissions,
            activities = activities,
            services = services,
            receivers = receivers,
            providers = providers,
            metaDataTags = emptyList(),
            usesFeature = usesFeature,
            usesLibrary = emptyList(),
            queries = queries,
            intentFilterSchemes = schemes,
        )
    }

    private fun parseComponents(manifest: String, tag: String): List<ManifestComponent> {
        val pattern = Regex("<$tag[\\s\\S]*?/>|<$tag[\\s\\S]*?</$tag>")
        return pattern.findAll(manifest).map { block ->
            val text = block.value
            parseComponentBlock(text)
        }.toList()
    }

    private fun parseComponentBlock(text: String): ManifestComponent {
        val aName = Regex("""android:name="([^"]*)"""").find(text)
        val aExported = Regex("""android:exported="(true|false)"""").find(text)
        val aTheme = Regex("""android:theme="([^"]*)"""").find(text)
        val aProcess = Regex("""android:process="([^"]*)"""").find(text)
        val aLaunchMode = Regex("""android:launchMode="([^"]*)"""").find(text)
        val aOrientation = Regex("""android:screenOrientation="([^"]*)"""").find(text)
        val aConfigChanges = Regex("""android:configChanges="([^"]*)"""").find(text)

        val intentFilters = parseIntentFilters(text)

        return ManifestComponent(
            name = aName?.groupValues?.getOrNull(1) ?: "",
            exported = aExported?.groupValues?.getOrNull(1)?.toBooleanStrictOrNull(),
            theme = aTheme?.groupValues?.getOrNull(1) ?: "",
            process = aProcess?.groupValues?.getOrNull(1) ?: "",
            launchMode = aLaunchMode?.groupValues?.getOrNull(1) ?: "",
            orientation = aOrientation?.groupValues?.getOrNull(1) ?: "",
            configChanges = aConfigChanges?.groupValues?.getOrNull(1) ?: "",
            intentFilters = intentFilters,
        )
    }

    private fun parseIntentFilters(componentText: String): List<IntentFilter> {
        val ifPattern = Regex("<intent-filter[\\s\\S]*?</intent-filter>")
        return ifPattern.findAll(componentText).map { ifMatch ->
            val ifText = ifMatch.value
            val autoVerify = Regex("""android:autoVerify="true"""").containsMatchIn(ifText)

            val actions = Regex("<action[\\s\\S]*?android:name=\"([^\"]*)\"").findAll(ifText).map {
                it.groupValues[1]
            }.toList()

            val categories = Regex("<category[\\s\\S]*?android:name=\"([^\"]*)\"").findAll(ifText).map {
                it.groupValues[1]
            }.toList()

            val dataSchemes = Regex("""android:scheme="([^"]*)"""").findAll(ifText).map {
                it.groupValues[1]
            }.toList()

            val dataHosts = Regex("""android:host="([^"]*)"""").findAll(ifText).map {
                it.groupValues[1]
            }.toList()

            val dataPaths = Regex("""android:path(?:Pattern)?="([^"]*)"""").findAll(ifText).map {
                it.groupValues[1]
            }.toList()

            val dataMimeTypes = Regex("""android:mimeType="([^"]*)"""").findAll(ifText).map {
                it.groupValues[1]
            }.toList()

            IntentFilter(
                autoVerify = autoVerify,
                actions = actions,
                categories = categories,
                dataSchemes = dataSchemes,
                dataHosts = dataHosts,
                dataPaths = dataPaths,
                dataMimeTypes = dataMimeTypes,
            )
        }.toList()
    }

    private fun parseProviderBlocks(manifest: String): List<ManifestProvider> {
        val pattern = Regex("<provider[\\s\\S]*?/>|<provider[\\s\\S]*?</provider>")
        return pattern.findAll(manifest).map { block ->
            val text = block.value
            ManifestProvider(
                name = Regex("""android:name="([^"]*)"""").find(text)?.groupValues?.getOrNull(1) ?: "",
                authorities = Regex("""android:authorities="([^"]*)"""").find(text)?.groupValues?.getOrNull(1) ?: "",
                exported = Regex("""android:exported="(true|false)"""").find(text)?.groupValues?.getOrNull(1)?.toBooleanStrictOrNull(),
                readPermission = Regex("""android:readPermission="([^"]*)"""").find(text)?.groupValues?.getOrNull(1) ?: "",
                writePermission = Regex("""android:writePermission="([^"]*)"""").find(text)?.groupValues?.getOrNull(1) ?: "",
                grantUriPermissions = Regex("""android:grantUriPermissions="(true|false)"""").find(text)?.groupValues?.getOrNull(1)?.toBooleanStrictOrNull(),
            )
        }.toList()
    }

    private fun findBuildConfigValue(sourcesDir: File, field: String): String? {
        val buildConfigs = sourcesDir.walkTopDown()
            .filter { it.name == "BuildConfig.java" }
            .toList()
        for (file in buildConfigs) {
            try {
                val content = file.readText()
                val regex = Regex("""$field\s*=\s*"([^"]*)"""")
                val match = regex.find(content)
                if (match != null) return match.groupValues[1]
            } catch (_: Exception) {}
        }
        return null
    }

    private fun extractAttr(xml: String, attr: String): String {
        val regex = Regex("""$attr\s*=\s*"([^"]*)"""")
        return regex.find(xml)?.groupValues?.getOrNull(1) ?: ""
    }

    private fun resolveAppName(manifest: String, resDir: File): String {
        val labelRegex = Regex("""android:label="@string/([^"]*)"""")
        val labelName = labelRegex.find(manifest)?.groupValues?.getOrNull(1) ?: return ""
        val stringsFile = File(resDir, "values/strings.xml")
        if (!stringsFile.exists()) return labelName
        val content = stringsFile.readText()
        val stringRegex = Regex("""<string name="$labelName">([^<]*)</string>""")
        return stringRegex.find(content)?.groupValues?.getOrNull(1) ?: labelName
    }

    private fun resolveAppIcon(resDir: File): ByteArray? {
        if (!resDir.exists()) return null
        val manifestFile = File(resDir.parentFile, "AndroidManifest.xml")
        val manifestText = if (manifestFile.exists()) manifestFile.readText() else ""
        val mipmapRegex = Regex("""android:icon="@(drawable|mipmap)/([^"]*)"""")
        val iconName = mipmapRegex.find(manifestText)?.groupValues?.getOrNull(2) ?: return null

        val densities = listOf("xxxhdpi", "xxhdpi", "xhdpi", "hdpi", "mdpi")

        for (density in densities) {
            val iconFile = File(resDir, "mipmap-$density/${iconName}.png")
            if (iconFile.exists()) return iconFile.readBytes()
        }

        if (iconName != "ic_launcher") {
            for (density in densities) {
                val fallback = File(resDir, "mipmap-$density/ic_launcher.png")
                if (fallback.exists()) return fallback.readBytes()
            }
        }

        val anydpiXml = File(resDir, "mipmap-anydpi/$iconName.xml")
        if (anydpiXml.exists()) {
            val xmlContent = anydpiXml.readText()
            val drawableRefs = Regex("""android:drawable="@(mipmap|drawable)/([^"]*)"""")
                .findAll(xmlContent).map { it.groupValues[2] }.toList()

            val order = listOf(1, 0, 2)
            for (idx in order) {
                if (idx < drawableRefs.size) {
                    val name = drawableRefs[idx]
                    for (density in densities) {
                        val f = File(resDir, "mipmap-$density/$name.png")
                        if (f.exists()) return f.readBytes()
                    }
                }
            }
        }

        return null
    }
}
