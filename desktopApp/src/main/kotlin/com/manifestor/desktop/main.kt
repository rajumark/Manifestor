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
import com.manifestor.desktop.ui.components.SettingsDialog
import com.manifestor.desktop.ui.theme.ThemeOption
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.awt.FileDialog
import java.io.File
import java.io.FilenameFilter
import java.time.Duration
import java.time.Instant
import java.util.prefs.Preferences

private val prefs = Preferences.userRoot().node("com/manifestor/desktop")

fun main() = application {
    var currentScreen by remember { mutableStateOf(if (ToolManager.isJadxReady()) Screen.WELCOME else Screen.TOOL_SETUP) }
    var toolSetupState by remember { mutableStateOf(ToolSetupState.NOT_STARTED) }
    var downloadProgress by remember { mutableStateOf(0f) }
    var toolSetupError by remember { mutableStateOf<String?>(null) }
    var projectInfo by remember { mutableStateOf<ProjectInfo?>(null) }
    var apkPath by remember { mutableStateOf<String?>(null) }
    val isDragging = remember { mutableStateOf(false) }
    var projectName by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var errorDialogMessage by remember { mutableStateOf<String?>(null) }
    var themeOption by remember { mutableStateOf(savedTheme()) }
    var showSettings by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var projects by remember(currentScreen) { mutableStateOf(loadProjects()) }
    var isDecompiling by remember { mutableStateOf(false) }
    var decompileError by remember { mutableStateOf<String?>(null) }
    var decompileRetryTrigger by remember { mutableStateOf(0) }
    var decompileProgress by remember { mutableStateOf(0f) }
    var decompileStatusText by remember { mutableStateOf("") }
    var overviewData by remember { mutableStateOf(ApkOverviewData()) }
    var recentApks by remember { mutableStateOf(emptyList<RecentApk>()) }
    var sourceCodeEntries by remember { mutableStateOf<List<FileEntry>>(emptyList()) }
    var sourceCodeCurrentPath by remember { mutableStateOf("") }
    var sourceCodeSelectedFile by remember { mutableStateOf<String?>(null) }
    var sourceCodeFileContent by remember { mutableStateOf("") }
    var sourceCodeSearchResults by remember { mutableStateOf<List<FileEntry>?>(null) }
    var sourceCodeSearchQuery by remember { mutableStateOf("") }

    fun readSourceCodeFile(projectDir: String, relativePath: String): String {
        val file = File(File(projectDir, "jadx_result"), relativePath)
        return if (file.exists() && file.isFile) {
            try { file.readText() } catch (_: Exception) { "Unable to read file" }
        } else ""
    }

    fun searchSourceCodeFiles(projectDir: String, query: String) {
        sourceCodeSearchQuery = query
        if (query.isEmpty()) {
            sourceCodeSearchResults = null
            return
        }
        val baseDir = File(projectDir, "jadx_result")
        if (!baseDir.exists()) {
            sourceCodeSearchResults = emptyList()
            return
        }
        val results = mutableListOf<FileEntry>()
        baseDir.walkTopDown().forEach { file ->
            if (file != baseDir && file.name.contains(query, ignoreCase = true)) {
                val relativePath = file.relativeTo(baseDir).path
                results.add(FileEntry(
                    name = file.name,
                    isDirectory = file.isDirectory,
                    relativePath = relativePath,
                ))
            }
        }
        sourceCodeSearchResults = results.sortedWith(
            compareByDescending<FileEntry> { it.isDirectory }.thenBy { it.name }
        )
    }

    fun loadSourceCodeEntries(projectDir: String, relativePath: String) {
        val baseDir = File(File(projectDir, "jadx_result"), relativePath)
        if (!baseDir.exists()) {
            sourceCodeEntries = emptyList()
            return
        }
        sourceCodeEntries = baseDir.listFiles()?.map { file ->
            FileEntry(
                name = file.name,
                isDirectory = file.isDirectory,
                relativePath = if (relativePath.isEmpty()) file.name else "$relativePath/${file.name}",
            )
        }?.sortedWith(compareByDescending<FileEntry> { it.isDirectory }.thenBy { it.name })
            ?: emptyList()
    }

    LaunchedEffect(projectInfo) {
        if (projectInfo != null) {
            val projectDir = "${projectsDir}/${projectInfo!!.projectName}"
            loadSourceCodeEntries(projectDir, "")
            sourceCodeCurrentPath = ""
            sourceCodeSelectedFile = null
        }
    }

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
                                apkPath = normalizePath(path)
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
            LaunchedEffect(currentScreen, projectInfo, decompileRetryTrigger) {
                val info = projectInfo
                if (currentScreen == Screen.HOME && info != null && !info.jadxDone && !isDecompiling) {
                    isDecompiling = true
                    decompileError = null
                    decompileProgress = 0f
                    decompileStatusText = ""
                    val projectDir = File(projectsDir, info.projectName)
                    val apkFile = File(projectDir, info.apkFileName)
                    val outputDir = File(projectDir, "jadx_result").absolutePath
                    val error = ToolManager.decompileApk(apkFile.absolutePath, outputDir) { progress, text ->
                        decompileProgress = progress
                        decompileStatusText = text
                    }
                    if (error != null) {
                        decompileError = error
                    } else {
                        updateProjectJadxDone(info.projectName, true)
                        projectInfo = info.copy(jadxDone = true)
                        overviewData = ApkOverviewParser.parse("$projectsDir/${info.projectName}")
                    }
                    isDecompiling = false
                }
            }
            App(
                screen = currentScreen,
                apkPath = apkPath,
                isDragging = isDragging.value,
                projectName = projectName,
                onProjectNameChange = { projectName = it; errorMessage = null },
                onCreateProject = {
                    val error = createProject(apkPath, projectName)
                    if (error != null) {
                        if (error.contains("\n")) errorDialogMessage = error else errorMessage = error
                    } else {
                        projectInfo = ProjectInfo(
                            projectName = projectName.trim(),
                            apkFileName = File(normalizePath(apkPath ?: "")).name,
                            apkFullPath = normalizePath(apkPath ?: ""),
                            createdAt = java.time.LocalDateTime.now().toString(),
                            jadxDone = false,
                        )
                        currentScreen = Screen.HOME
                        projectName = ""
                    }
                },
                errorMessage = errorMessage,
                errorDialogMessage = errorDialogMessage,
                onDismissErrorDialog = { errorDialogMessage = null },
                themeOption = themeOption,
                onSettingsClick = { showSettings = true },
                onBrowseClick = {
                    val dialog = FileDialog(window, "Select APK", FileDialog.LOAD)
                    dialog.filenameFilter = FilenameFilter { _, name -> name.lowercase().endsWith(".apk") }
                    dialog.isVisible = true
                    dialog.file?.let { fileName ->
                        apkPath = normalizePath(File(dialog.directory, fileName).absolutePath)
                        projectName = ""
                        errorMessage = null
                    }
                },
                onClearApk = { apkPath = null; projectName = ""; errorMessage = null },
                projectInfo = projectInfo,
                onNavigateHome = { currentScreen = Screen.HOME },
                onNavigateWelcome = { currentScreen = Screen.WELCOME },
                toolSetupState = toolSetupState,
                downloadProgress = downloadProgress,
                toolSetupError = toolSetupError,
                onToolDownload = {
                    toolSetupState = ToolSetupState.DOWNLOADING
                    downloadProgress = 0f
                    toolSetupError = null
                    scope.launch {
                        try {
                            ToolManager.downloadJadx { progress ->
                                downloadProgress = progress
                            }
                            toolSetupState = ToolSetupState.EXTRACTING
                            ToolManager.extractJadx { }
                            toolSetupState = ToolSetupState.COMPLETED
                            currentScreen = Screen.WELCOME
                        } catch (e: Exception) {
                            toolSetupState = ToolSetupState.ERROR
                            toolSetupError = e.message ?: "Download failed"
                        }
                    }
                },
                isDecompiling = isDecompiling,
                decompileError = decompileError,
                decompileProgress = decompileProgress,
                decompileStatusText = decompileStatusText,
                onRetryDecompile = { decompileRetryTrigger++ },
                sourceCodeEntries = sourceCodeEntries,
                sourceCodeCurrentPath = sourceCodeCurrentPath,
                sourceCodeSelectedFile = sourceCodeSelectedFile,
                sourceCodeSearchResults = sourceCodeSearchResults,
                onSourceCodeNavigate = { path ->
                    val projectDir = projectInfo?.let { "${projectsDir}/${it.projectName}" } ?: return@App
                    sourceCodeCurrentPath = path
                    sourceCodeSelectedFile = null
                    loadSourceCodeEntries(projectDir, path)
                },
                onSourceCodeBack = {
                    val projectDir = projectInfo?.let { "${projectsDir}/${it.projectName}" } ?: return@App
                    val parts = sourceCodeCurrentPath.split("/")
                    val parent = parts.dropLast(1).joinToString("/")
                    sourceCodeCurrentPath = parent
                    sourceCodeSelectedFile = null
                    loadSourceCodeEntries(projectDir, parent)
                },
                sourceCodeFileContent = sourceCodeFileContent,
                onSourceCodeFileClick = { path ->
                    sourceCodeSelectedFile = path
                    val projectDir = projectInfo?.let { "${projectsDir}/${it.projectName}" } ?: return@App
                    sourceCodeFileContent = readSourceCodeFile(projectDir, path)
                },
                onSourceCodeDownload = {
                    val projectDir = projectInfo?.let { "${projectsDir}/${it.projectName}" } ?: return@App
                    val path = sourceCodeSelectedFile ?: return@App
                    val srcFile = File(File(projectDir, "jadx_result"), path)
                    if (!srcFile.exists()) return@App
                    val home = System.getProperty("user.home")
                    val downloadsDir = if (System.getProperty("os.name").startsWith("Windows"))
                        "${System.getenv("USERPROFILE")}\\Downloads"
                    else "$home/Downloads"
                    val destDir = File(downloadsDir)
                    if (!destDir.exists()) destDir.mkdirs()
                    var destFile = File(destDir, srcFile.name)
                    var counter = 1
                    while (destFile.exists()) {
                        val name = srcFile.nameWithoutExtension
                        val ext = srcFile.extension
                        destFile = File(destDir, "${name}($counter).$ext")
                        counter++
                    }
                    srcFile.copyTo(destFile, overwrite = false)
                },
                onSourceCodeCopy = {
                    sourceCodeSelectedFile?.let { path ->
                        val projectDir = projectInfo?.let { "${projectsDir}/${it.projectName}" } ?: return@App
                        val content = readSourceCodeFile(projectDir, path)
                        val clipboard = java.awt.Toolkit.getDefaultToolkit().systemClipboard
                        clipboard.setContents(java.awt.datatransfer.StringSelection(content), null)
                    }
                },
                onSourceCodeSearchFile = { query ->
                    val projectDir = projectInfo?.let { "${projectsDir}/${it.projectName}" } ?: return@App
                    searchSourceCodeFiles(projectDir, query)
                },
                onSourceCodeSearchContent = { /* handled locally in SourceCodePage */ },
                overviewData = overviewData,
                projects = projects,
                onProjectClick = { project ->
                    projectInfo = ProjectInfo(
                        projectName = project.projectName,
                        apkFileName = project.apkFileName,
                        apkFullPath = project.apkFullPath,
                        createdAt = project.createdAt,
                        jadxDone = project.jadxDone,
                    )
                    if (project.jadxDone) {
                        overviewData = ApkOverviewParser.parse("$projectsDir/${project.projectName}")
                    }
                    currentScreen = Screen.HOME
                },
                recentApks = recentApks,
                onRecentApkClick = { path ->
                    apkPath = normalizePath(path)
                    projectName = ""
                    errorMessage = null
                },
                onRecentApksRefresh = { recentApks = loadRecentApks() },
                onProjectDelete = { project ->
                    val projectDir = File(projectsDir, project.projectName)
                    if (projectDir.exists()) projectDir.deleteRecursively()
                    projects = loadProjects()
                },
                onProjectOpenFolder = { project ->
                    val projectDir = File(projectsDir, project.projectName)
                    if (projectDir.exists()) {
                        java.awt.Desktop.getDesktop().open(projectDir)
                    }
                },
                onProjectCopyPath = { project ->
                    val clip = java.awt.Toolkit.getDefaultToolkit().systemClipboard
                    clip.setContents(
                        java.awt.datatransfer.StringSelection(File(projectsDir, project.projectName).absolutePath),
                        null,
                    )
                },
            )

            if (showSettings) {
                SettingsDialog(
                    themeOption = themeOption,
                    onThemeChange = { option ->
                        themeOption = option
                        saveTheme(option)
                    },
                    onDismiss = { showSettings = false },
                )
            }
        }
    }
}

private fun savedTheme(): ThemeOption =
    try { ThemeOption.valueOf(prefs.get("theme", "DARK")) } catch (_: Exception) { ThemeOption.DARK }

private fun saveTheme(option: ThemeOption) { prefs.put("theme", option.name) }

private val projectsDir: String by lazy {
    val os = System.getProperty("os.name")
    val home = System.getProperty("user.home")
    val base = when {
        os.startsWith("Windows") -> "${System.getenv("APPDATA")}\\Manifestor"
        os == "Mac OS X" -> "$home/Library/Application Support/Manifestor"
        else -> {
            val xdg = System.getenv("XDG_DATA_HOME") ?: "$home/.local/share"
            "$xdg/Manifestor"
        }
    }
    "$base/user_projects"
}

private fun normalizePath(path: String): String {
    val cleaned = path.removePrefix("file:").removePrefix("//")
    return cleaned
}

private fun createProject(apkPath: String?, projectName: String): String? {
    if (apkPath == null) return null

    val name = projectName.trim()
    if (name.isEmpty()) return "Project name cannot be empty"
    if (!name.first().isLetter()) return "Project name must start with a letter"

    val projectDir = File(projectsDir, name)
    val apkFile = File(normalizePath(apkPath))

    if (projectDir.exists()) return "Project '$name' already exists"

    if (!apkFile.exists()) return "APK file not found at:\n$apkPath"

    if (!projectDir.mkdirs()) {
        val parentOk = File(projectsDir).exists() || File(projectsDir).mkdirs()
        if (!parentOk) return "Failed to create project folder.\nCheck write permission for:\n$projectsDir"
    }

    val destFile = File(projectDir, apkFile.name)
    try {
        apkFile.copyTo(destFile, overwrite = false)
    } catch (e: Exception) {
        projectDir.deleteRecursively()
        return "Failed to copy APK:\n${e.message ?: "unknown error"}"
    }

    val metadata = buildString {
        appendLine("{")
        appendLine("  \"projectName\": \"$name\",")
        appendLine("  \"apkFileName\": \"${apkFile.name}\",")
        appendLine("  \"apkFullPath\": \"${apkFile.absolutePath}\",")
        appendLine("  \"createdAt\": \"${java.time.LocalDateTime.now()}\",")
        appendLine("  \"jadxDone\": false")
        appendLine("}")
    }
    File(projectDir, "project.json").writeText(metadata)

    return null
}

private fun loadProjects(): List<ProjectSummary> {
    val dir = File(projectsDir)
    if (!dir.exists()) return emptyList()
    return dir.listFiles()?.filter { it.isDirectory }?.mapNotNull { projectDir ->
        val jsonFile = File(projectDir, "project.json")
        if (!jsonFile.exists()) return@mapNotNull null
        try {
            val json = jsonFile.readText()
            val name = extractJsonString(json, "projectName") ?: return@mapNotNull null
            val apkFileName = extractJsonString(json, "apkFileName") ?: ""
            val apkFullPath = extractJsonString(json, "apkFullPath") ?: ""
            val createdAt = extractJsonString(json, "createdAt") ?: ""
            val jadxDone = extractJsonBool(json, "jadxDone") ?: false
            ProjectSummary(
                projectName = name, apkFileName = apkFileName,
                apkFullPath = apkFullPath, createdAt = createdAt,
                createdAtDisplay = daysAgoText(createdAt),
                jadxDone = jadxDone,
            )
        } catch (_: Exception) { null }
    }?.sortedByDescending { it.createdAt } ?: emptyList()
}

private fun extractJsonString(json: String, key: String): String? {
    val regex = "\"$key\"\\s*:\\s*\"([^\"]*)\"".toRegex()
    return regex.find(json)?.groupValues?.get(1)
}

private fun daysAgoText(createdAt: String): String {
    return try {
        val created = java.time.LocalDateTime.parse(createdAt)
        val now = java.time.LocalDateTime.now()
        val days = java.time.Duration.between(created, now).toDays()
        when {
            days < 0 -> "Just now"
            days == 0L -> "Today"
            days == 1L -> "1 day ago"
            else -> "$days days ago"
        }
    } catch (_: Exception) { createdAt }
}

private fun extractJsonBool(json: String, key: String): Boolean? {
    val regex = "\"$key\"\\s*:\\s*(true|false)".toRegex()
    return regex.find(json)?.groupValues?.get(1)?.toBooleanStrictOrNull()
}

private fun updateProjectJadxDone(projectName: String, done: Boolean) {
    val jsonFile = File(File(projectsDir, projectName), "project.json")
    if (!jsonFile.exists()) return
    try {
        val json = jsonFile.readText()
        val updated = if ("\"jadxDone\"" in json) {
            json.replace(Regex("\"jadxDone\"\\s*:\\s*(true|false)"), "\"jadxDone\": $done")
        } else {
            json.trimEnd().trimEnd('}') + ",\n  \"jadxDone\": $done\n}\n"
        }
        jsonFile.writeText(updated)
    } catch (_: Exception) {}
}

private fun relativeTimeText(lastModified: Long): String {
    val now = Instant.now()
    val modified = Instant.ofEpochMilli(lastModified)
    val duration = Duration.between(modified, now)
    val minutes = duration.toMinutes()
    val hours = duration.toHours()
    val days = duration.toDays()
    return when {
        minutes < 1 -> "Just now"
        minutes < 60 -> if (minutes == 1L) "1 minute ago" else "$minutes minutes ago"
        hours < 24 -> if (hours == 1L) "1 hour ago" else "$hours hours ago"
        days < 7 -> if (days == 1L) "1 day ago" else "$days days ago"
        days < 30 -> if (days / 7 == 1L) "1 week ago" else "${days / 7} weeks ago"
        days < 365 -> if (days / 30 == 1L) "1 month ago" else "${days / 30} months ago"
        else -> if (days / 365 == 1L) "1 year ago" else "${days / 365} years ago"
    }
}

private fun loadRecentApks(): List<RecentApk> {
    val home = System.getProperty("user.home")
    val downloadsPath = if (System.getProperty("os.name").startsWith("Windows"))
        "${System.getenv("USERPROFILE")}\\Downloads"
    else "$home/Downloads"
    val dir = File(downloadsPath)
    if (!dir.exists()) return emptyList()
    return dir.listFiles()
        ?.filter { it.isFile && it.name.lowercase().endsWith(".apk") }
        ?.map {
            RecentApk(it.absolutePath, it.name, it.lastModified(), relativeTimeText(it.lastModified()))
        }
        ?.sortedByDescending { it.lastModified }
        ?: emptyList()
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
