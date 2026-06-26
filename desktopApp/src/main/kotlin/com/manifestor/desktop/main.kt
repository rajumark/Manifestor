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
                onBrowseClick = {
                    val dialog = FileDialog(window, "Select APK", FileDialog.LOAD)
                    dialog.filenameFilter = FilenameFilter { _, name -> name.lowercase().endsWith(".apk") }
                    dialog.isVisible = true
                    dialog.file?.let { fileName ->
                        apkPath = File(dialog.directory, fileName).absolutePath
                    }
                },
                onClearApk = { apkPath = null },
            )
        }
    }
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
