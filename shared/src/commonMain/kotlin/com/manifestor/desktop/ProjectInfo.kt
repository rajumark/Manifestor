package com.manifestor.desktop

data class ProjectInfo(
    val projectName: String,
    val apkFileName: String,
    val apkFullPath: String,
    val createdAt: String,
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
