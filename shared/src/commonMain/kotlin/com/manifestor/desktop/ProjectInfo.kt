package com.manifestor.desktop

data class ProjectInfo(
    val projectName: String,
    val apkFileName: String,
    val apkFullPath: String,
    val createdAt: String,
)

enum class Screen {
    WELCOME, HOME,
}
