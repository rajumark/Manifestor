package com.manifestor.desktop

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.util.zip.ZipInputStream

object ToolManager {

    val toolsDir: String by lazy {
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
        "$base/tools"
    }

    fun isJadxReady(): Boolean = File(toolsDir, "donejadx.txt").exists()

    suspend fun downloadJadx(onProgress: (Float) -> Unit): File = withContext(Dispatchers.IO) {
        File(toolsDir).mkdirs()

        val zipFile = File(toolsDir, "jadx-1.5.5.zip")
        try {
            val url = URL("https://github.com/rajumark/adbcontent/raw/main/jadx-1.5.5.zip")
            val connection = url.openConnection()
            connection.connectTimeout = 10000
            connection.readTimeout = 15000
            val contentLength = connection.contentLengthLong
            val inputStream = connection.getInputStream()

            FileOutputStream(zipFile).use { output ->
                val buffer = ByteArray(8192)
                var totalRead = 0L
                var read: Int
                while (inputStream.read(buffer).also { read = it } != -1) {
                    output.write(buffer, 0, read)
                    totalRead += read
                    if (contentLength > 0) {
                        onProgress(totalRead.toFloat() / contentLength)
                    }
                }
            }
            inputStream.close()
            onProgress(1f)
            zipFile
        } catch (e: Exception) {
            zipFile.delete()
            throw e
        }
    }

    suspend fun extractJadx(onProgress: (Float) -> Unit) = withContext(Dispatchers.IO) {
        val zipFile = File(toolsDir, "jadx-1.5.5.zip")
        ZipInputStream(zipFile.inputStream()).use { zis ->
            var entry = zis.nextEntry
            while (entry != null) {
                val file = File(toolsDir, entry.name)
                if (entry.isDirectory) {
                    file.mkdirs()
                } else {
                    file.parentFile.mkdirs()
                    file.outputStream().use { it.write(zis.readBytes()) }
                }
                zis.closeEntry()
                entry = zis.nextEntry
            }
        }
        zipFile.delete()
        File(toolsDir, "donejadx.txt").writeText("done")
        onProgress(1f)
    }
}
