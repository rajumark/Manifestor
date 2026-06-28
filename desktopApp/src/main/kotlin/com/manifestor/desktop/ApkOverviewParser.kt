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

    private fun parseManifestCategories(manifest: String): ManifestCategories {
        val permissions = Regex("<uses-permission").findAll(manifest).count()
        val activities = Regex("<activity[\\s>]").findAll(manifest).count()
        val services = Regex("<service[\\s>]").findAll(manifest).count()
        val receivers = Regex("<receiver[\\s>]").findAll(manifest).count()
        val providers = Regex("<provider[\\s>]").findAll(manifest).count()
        val metaDataTags = Regex("<meta-data").findAll(manifest).count()

        val attrNameRegex = Regex("""android:name="([^"]*)"""")
        val usesFeature = Regex("<uses-feature[\\s\\S]*?</uses-feature>").findAll(manifest).map { block ->
            attrNameRegex.find(block.value)?.groupValues?.getOrNull(1) ?: ""
        }.filter { it.isNotEmpty() }.toList()

        val usesLibrary = Regex("<uses-library[\\s\\S]*?</uses-library>").findAll(manifest).map { block ->
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
            metaDataTags = metaDataTags,
            usesFeature = usesFeature,
            usesLibrary = usesLibrary,
            queries = queries,
            intentFilterSchemes = schemes,
        )
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

        // 1. Try direct PNG in density folders
        for (density in densities) {
            val iconFile = File(resDir, "mipmap-$density/${iconName}.png")
            if (iconFile.exists()) return iconFile.readBytes()
        }

        // 2. Try legacy ic_launcher.png fallback before adaptive XML
        if (iconName != "ic_launcher") {
            for (density in densities) {
                val fallback = File(resDir, "mipmap-$density/ic_launcher.png")
                if (fallback.exists()) return fallback.readBytes()
            }
        }

        // 3. Try adaptive icon XML in mipmap-anydpi
        val anydpiXml = File(resDir, "mipmap-anydpi/$iconName.xml")
        if (anydpiXml.exists()) {
            val xmlContent = anydpiXml.readText()
            // Search for all drawable references with their type
            val drawableRefs = Regex("""android:drawable="@(mipmap|drawable)/([^"]*)"""")
                .findAll(xmlContent).map { it.groupValues[2] }.toList()

            // try foreground first (index 1), then background (index 0), then monochrome (index 2)
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
