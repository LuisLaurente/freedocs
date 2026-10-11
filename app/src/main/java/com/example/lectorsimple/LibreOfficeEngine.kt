package com.example.lectorsimple

import android.app.Activity
import android.util.Log
import org.libreoffice.kit.Document
import org.libreoffice.kit.LibreOfficeKit
import org.libreoffice.kit.Office
import java.io.File
import java.io.InterruptedIOException

/**
 * Encapsulates LibreOfficeKit initialization and local offline document conversion.
 * All operations run on background threads (ReaderWorker).
 */
object LibreOfficeEngine {
    private const val TAG = "LibreOfficeEngine"
    private var initialized = false

    @Synchronized
    fun ensureInitialized(activity: Activity) {
        if (initialized) return
        try {
            Log.i(TAG, "Inicializando LibreOfficeKit...")
            LibreOfficeKit.putenv("SAL_LOG=+WARN+INFO")
            LibreOfficeKit.putenv("SAL_LOK_OPTIONS=compact_fonts")
            LibreOfficeKit.init(activity)
            initialized = true
            Log.i(TAG, "LibreOfficeKit inicializado exitosamente.")
        } catch (t: Throwable) {
            Log.e(TAG, "Error inicializando LibreOfficeKit", t)
            throw ReadingException("No se pudo iniciar el motor de lectura por hojas: ${t.message}")
        }
    }

    /**
     * Converts a local .doc or .docx file to a temporary PDF file for sheet display.
     * Must be called from a worker thread.
     */
    @Synchronized
    fun convertWordToPdf(activity: Activity, sourceWordFile: File, outputPdfFile: File) {
        checkCancelled()
        ensureInitialized(activity)
        checkCancelled()

        val handle = LibreOfficeKit.getLibreOfficeKitHandle()
            ?: throw ReadingException("El motor de LibreOffice no proporcionó un controlador válido.")

        val office = Office(handle)
        val sourcePath = sourceWordFile.absolutePath

        val doc: Document? = try {
            office.documentLoad(sourcePath)
                ?: office.documentLoad("file://$sourcePath")
        } catch (t: Throwable) {
            Log.e(TAG, "Fallo al cargar documento en LibreOffice", t)
            throw ReadingException("Error al abrir el documento con LibreOffice: ${t.message}")
        }

        if (doc == null) {
            val error = office.error ?: "Formato o estructura no reconocidos"
            Log.e(TAG, "LibreOffice documentLoad retornó null: $error")
            throw ReadingException("No se pudo abrir el documento Word: $error")
        }

        try {
            checkCancelled()
            val outputPath = outputPdfFile.absolutePath
            Log.i(TAG, "Convirtiendo documento a PDF: $outputPath")
            doc.saveAs(outputPath, "pdf", null)
            checkCancelled()
            if (!outputPdfFile.exists() || outputPdfFile.length() == 0L) {
                // Intento alternativo con esquema URI si la ruta plana no generó archivo
                doc.saveAs("file://$outputPath", "pdf", null)
            }
        } catch (interrupted: InterruptedException) {
            outputPdfFile.delete()
            throw InterruptedIOException("Conversión cancelada")
        } catch (interrupted: InterruptedIOException) {
            outputPdfFile.delete()
            throw interrupted
        } catch (t: Throwable) {
            outputPdfFile.delete()
            Log.e(TAG, "Error guardando documento como PDF", t)
            throw ReadingException("Error al generar la vista por hojas: ${t.message}")
        } finally {
            try {
                doc.destroy()
            } catch (e: Throwable) {
                Log.w(TAG, "Error al destruir instancia del documento LOK", e)
            }
        }

        if (!outputPdfFile.exists() || outputPdfFile.length() == 0L) {
            outputPdfFile.delete()
            throw ReadingException("No se pudo generar el archivo de visualización por hojas.")
        }
    }

    private fun checkCancelled() {
        if (Thread.currentThread().isInterrupted) throw InterruptedIOException("Operación cancelada")
    }
}
