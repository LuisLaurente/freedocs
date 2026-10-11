package com.example.lectorsimple

import android.content.Context
import android.content.res.AssetManager
import android.util.AtomicFile
import java.io.File
import java.io.IOException
import java.io.InterruptedIOException

/** Prepares the real filesystem paths referenced by LibreOffice's unorc and sofficerc. */
internal object LibreOfficeResources {
    @Synchronized
    fun prepare(context: Context) {
        val assets = context.assets
        val files = mutableListOf<String>()
        collectFiles(assets, "unpack", files)
        val required = listOf(
            "program/sofficerc", "program/udkapi.rdb", "program/offapi.rdb",
            "program/oovbaapi.rdb", "etc/fonts/fonts.conf"
        )
        if (!files.containsAll(required) || files.none { it.startsWith("user/fonts/") }) {
            throw IOException("El paquete de recursos de LibreOffice está incompleto.")
        }
        // These stay inside the APK: native code accesses them through /assets.
        listOf(
            "program/fundamentalrc", "program/unorc", "program/bootstraprc",
            "program/services.rdb", "program/services/services.rdb", "share/registry/main.xcd"
        ).forEach { path ->
            assets.open(path).use { if (it.read() < 0) throw IOException("Recurso vacío: $path") }
        }

        val dataDir = File(context.applicationInfo.dataDir)
        val apk = File(context.applicationInfo.sourceDir)
        @Suppress("DEPRECATION")
        val updateTime = context.packageManager.getPackageInfo(context.packageName, 0).lastUpdateTime
        // Includes APK identity so a development reinstall with the same version also refreshes assets.
        val version = "1:$updateTime:${apk.absolutePath}:${apk.length()}:${apk.lastModified()}"
        val preferences = context.getSharedPreferences("libreoffice-resources", Context.MODE_PRIVATE)
        val ready = preferences.getString("version", null) == version && files.all { path ->
            val file = File(dataDir, path)
            file.isFile && file.length() > 0 && file.length() == preferences.getLong(path, -1)
        }
        if (ready) return

        // Invalidate first. Interrupted/failed extraction must be retried before entering JNI.
        if (!preferences.edit().clear().commit()) throw IOException("No se pudo preparar LibreOffice.")
        val completed = preferences.edit()
        val buffer = ByteArray(32 * 1024)
        files.forEach { path ->
            checkCancelled()
            val destination = File(dataDir, path)
            val parent = destination.parentFile ?: throw IOException("Ruta de recursos inválida.")
            if (!parent.isDirectory && !parent.mkdirs()) throw IOException("No se pudo crear $path")
            val atomic = AtomicFile(destination)
            val output = atomic.startWrite()
            var copiedBytes = 0L
            try {
                assets.open("unpack/$path").use { input ->
                    while (true) {
                        checkCancelled()
                        val count = input.read(buffer)
                        if (count < 0) break
                        output.write(buffer, 0, count)
                        copiedBytes += count
                    }
                }
                if (output.channel.size() == 0L) throw IOException("Recurso vacío: $path")
                atomic.finishWrite(output)
            } catch (failure: Throwable) {
                atomic.failWrite(output)
                throw failure
            }
            if (!destination.isFile || copiedBytes == 0L || destination.length() != copiedBytes) {
                throw IOException("No se pudo extraer $path")
            }
            completed.putLong(path, destination.length())
        }
        checkCancelled()
        if (!completed.putString("version", version).commit()) {
            throw IOException("No se pudo guardar la preparación de LibreOffice.")
        }
    }

    private fun collectFiles(assets: AssetManager, directory: String, files: MutableList<String>) {
        checkCancelled()
        val children = assets.list(directory) ?: throw IOException("Faltan recursos: $directory")
        if (children.isEmpty()) {
            files.add(directory.removePrefix("unpack/"))
        } else {
            children.forEach { collectFiles(assets, "$directory/$it", files) }
        }
    }

    private fun checkCancelled() {
        if (Thread.currentThread().isInterrupted) throw InterruptedIOException("Preparación cancelada")
    }
}
