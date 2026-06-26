package com.manifestor.desktop

import androidx.compose.runtime.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import java.awt.FileDialog
import java.awt.datatransfer.DataFlavor
import java.awt.dnd.*
import java.io.File
import java.io.FilenameFilter

fun main() = application {
    var apkPath by remember { mutableStateOf<String?>(null) }
    var isDragging by remember { mutableStateOf(false) }

    Window(
        onCloseRequest = ::exitApplication,
        title = "Manifestor",
    ) {
        DisposableEffect(Unit) {
            val contentPane = window.contentPane
            val dropTarget = DropTarget(
                contentPane,
                object : DropTargetAdapter() {
                    override fun dragEnter(event: DropTargetDragEvent) {
                        isDragging = true
                    }
                    override fun dragExit(event: DropTargetEvent) {
                        isDragging = false
                    }
                    override fun drop(event: DropTargetDropEvent) {
                        isDragging = false
                        event.acceptDrop(DnDConstants.ACTION_COPY)
                        try {
                            @Suppress("UNCHECKED_CAST")
                            val files = event.transferable
                                .getTransferData(DataFlavor.javaFileListFlavor) as List<File>
                            files.firstOrNull { it.extension.lowercase() == "apk" }?.let {
                                apkPath = it.absolutePath
                            }
                        } catch (_: Exception) {
                            event.rejectDrop()
                        }
                        event.dropComplete(true)
                    }
                }
            )
            onDispose {
                dropTarget.component = null
            }
        }

        App(
            apkPath = apkPath,
            isDragging = isDragging,
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
