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
    val permissions: Int = 0,
    val activities: Int = 0,
    val services: Int = 0,
    val receivers: Int = 0,
    val providers: Int = 0,
    val metaDataTags: Int = 0,
    val usesFeature: List<String> = emptyList(),
    val usesLibrary: List<String> = emptyList(),
    val queries: List<String> = emptyList(),
    val intentFilterSchemes: List<String> = emptyList(),
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
