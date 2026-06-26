@file:OptIn(ExperimentalComposeUiApi::class)

package com.manifestor.desktop

import androidx.compose.ui.ExperimentalComposeUiApi

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.DragAndDropTargetModifierNode
import androidx.compose.ui.draganddrop.DragData
import androidx.compose.ui.draganddrop.dragData
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import java.awt.FileDialog
import java.io.File
import java.io.FilenameFilter

fun main() = application {
    var apkPath by remember { mutableStateOf<String?>(null) }
    val isDragging = remember { mutableStateOf(false) }
    var projectName by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Window(
        onCloseRequest = ::exitApplication,
        title = "Manifestor",
    ) {
        val target = remember {
            object : DragAndDropTarget {
                override fun onDrop(event: DragAndDropEvent): Boolean {
                    isDragging.value = false
                    val data = event.dragData()
                    if (data is DragData.FilesList) {
                        try {
                            val files = data.readFiles()
                            files.firstOrNull { it.lowercase().endsWith(".apk") }?.let { path ->
                                apkPath = path
                                projectName = ""
                                errorMessage = null
                            }
                        } catch (_: Exception) {
                            // macOS may not have resolved file data yet
                        }
                    }
                    return true
                }
                override fun onEntered(event: DragAndDropEvent) {
                    isDragging.value = true
                }
                override fun onExited(event: DragAndDropEvent) {
                    isDragging.value = false
                }
                override fun onEnded(event: DragAndDropEvent) {
                    isDragging.value = false
                }
            }
        }

        val shouldStart = remember {
            { event: DragAndDropEvent ->
                val data = event.dragData()
                data is DragData.FilesList
            }
        }

        Box(
            Modifier
                .fillMaxSize()
                .then(FileDropNodeElement(shouldStart, target))
        ) {
            App(
                apkPath = apkPath,
                isDragging = isDragging.value,
                projectName = projectName,
                onProjectNameChange = { projectName = it; errorMessage = null },
                onCreateProject = {
                    val error = createProject(apkPath, projectName)
                    if (error != null) errorMessage = error else projectName = ""
                },
                errorMessage = errorMessage,
                onBrowseClick = {
                    val dialog = FileDialog(window, "Select APK", FileDialog.LOAD)
                    dialog.filenameFilter = FilenameFilter { _, name -> name.lowercase().endsWith(".apk") }
                    dialog.isVisible = true
                    dialog.file?.let { fileName ->
                        apkPath = File(dialog.directory, fileName).absolutePath
                        projectName = ""
                        errorMessage = null
                    }
                },
                onClearApk = { apkPath = null; projectName = ""; errorMessage = null },
            )
        }
    }
}

private val appDataDir: String by lazy {
    val os = System.getProperty("os.name")
    val home = System.getProperty("user.home")
    when {
        os.startsWith("Windows") -> "${System.getenv("APPDATA")}\\Manifestor"
        os == "Mac OS X" -> "$home/Library/Application Support/Manifestor"
        else -> {
            val xdg = System.getenv("XDG_DATA_HOME") ?: "$home/.local/share"
            "$xdg/Manifestor"
        }
    }
}

private fun createProject(apkPath: String?, projectName: String): String? {
    if (apkPath == null) return null

    val name = projectName.trim()
    if (name.isEmpty()) return "Project name cannot be empty"
    if (!name.first().isLetter()) return "Project name must start with a letter"

    val projectDir = File(appDataDir, name)

    if (projectDir.exists()) return "Project '$name' already exists"

    projectDir.mkdirs()

    val apkFile = File(apkPath)
    val destFile = File(projectDir, apkFile.name)
    try {
        apkFile.copyTo(destFile, overwrite = false)
    } catch (_: Exception) {
        projectDir.deleteRecursively()
        return "Failed to copy APK file"
    }

    val metadata = buildString {
        appendLine("{")
        appendLine("  \"projectName\": \"$name\",")
        appendLine("  \"apkFileName\": \"${apkFile.name}\",")
        appendLine("  \"apkFullPath\": \"${apkFile.absolutePath}\",")
        appendLine("  \"createdAt\": \"${java.time.LocalDateTime.now()}\"")
        appendLine("}")
    }
    File(projectDir, "project.json").writeText(metadata)

    return null
}

private class FileDropNodeElement(
    private val shouldStart: (DragAndDropEvent) -> Boolean,
    private val target: DragAndDropTarget,
) : ModifierNodeElement<Modifier.Node>() {
    override fun create(): Modifier.Node =
        DragAndDropTargetModifierNode(shouldStart, target) as Modifier.Node
    override fun update(node: Modifier.Node) {}
    override fun hashCode(): Int = System.identityHashCode(this)
    override fun equals(other: Any?): Boolean = this === other
}
