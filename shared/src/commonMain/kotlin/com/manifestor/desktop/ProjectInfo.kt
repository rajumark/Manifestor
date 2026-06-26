package com.manifestor.desktop

data class ProjectInfo(
    val projectName: String,
    val apkFileName: String,
    val apkFullPath: String,
    val createdAt: String,
)

data class ProjectSummary(
    val projectName: String,
    val apkFileName: String,
    val apkFullPath: String,
    val createdAt: String,
    val createdAtDisplay: String,
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
