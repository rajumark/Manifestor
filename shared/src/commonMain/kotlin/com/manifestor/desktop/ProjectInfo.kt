package com.manifestor.desktop

data class ProjectInfo(
    val projectName: String,
    val apkFileName: String,
    val apkFullPath: String,
    val createdAt: String,
    val jadxDone: Boolean = false,
)

data class ProjectSummary(
    val projectName: String,
    val apkFileName: String,
    val apkFullPath: String,
    val createdAt: String,
    val createdAtDisplay: String,
    val jadxDone: Boolean = false,
)

data class RecentApk(
    val filePath: String,
    val fileName: String,
    val lastModified: Long,
    val relativeTime: String,
)

data class FileEntry(
    val name: String,
    val isDirectory: Boolean,
    val relativePath: String,
)

data class ApkOverviewData(
    val appName: String = "",
    val appIconBytes: ByteArray? = null,
    val appIdentity: AppIdentity = AppIdentity(),
    val manifestCategories: ManifestCategories = ManifestCategories(),
)

data class AppIdentity(
    val packageName: String = "",
    val versionName: String = "",
    val versionCode: String = "",
    val minSdk: String = "",
    val targetSdk: String = "",
    val buildFlavor: String = "",
)

data class ManifestCategories(
    val permissions: List<ManifestPermission> = emptyList(),
    val activities: List<ManifestComponent> = emptyList(),
    val services: List<ManifestComponent> = emptyList(),
    val receivers: List<ManifestComponent> = emptyList(),
    val providers: List<ManifestProvider> = emptyList(),
    val metaDataTags: List<String> = emptyList(),
    val usesFeature: List<String> = emptyList(),
    val usesLibrary: List<String> = emptyList(),
    val queries: List<String> = emptyList(),
    val intentFilterSchemes: List<String> = emptyList(),
)

data class ManifestPermission(
    val name: String,
    val isDangerous: Boolean,
)

data class ManifestComponent(
    val name: String,
    val exported: Boolean? = null,
    val theme: String = "",
    val process: String = "",
    val launchMode: String = "",
    val orientation: String = "",
    val configChanges: String = "",
    val intentFilters: List<IntentFilter> = emptyList(),
)

data class ManifestProvider(
    val name: String,
    val authorities: String = "",
    val exported: Boolean? = null,
    val readPermission: String = "",
    val writePermission: String = "",
    val grantUriPermissions: Boolean? = null,
)

data class IntentFilter(
    val autoVerify: Boolean = false,
    val actions: List<String> = emptyList(),
    val categories: List<String> = emptyList(),
    val dataSchemes: List<String> = emptyList(),
    val dataHosts: List<String> = emptyList(),
    val dataPaths: List<String> = emptyList(),
    val dataMimeTypes: List<String> = emptyList(),
)

enum class Screen {
    TOOL_SETUP, WELCOME, HOME,
}

enum class ToolSetupState {
    NOT_STARTED,
    DOWNLOADING,
    EXTRACTING,
    COMPLETED,
    ERROR,
}
