package com.example.lectorsimple

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Xml
import org.apache.poi.hwpf.HWPFDocument
import org.apache.poi.hwpf.HWPFOldDocument
import org.apache.poi.hwpf.OldWordFileFormatException
import org.apache.poi.hwpf.usermodel.Range
import org.apache.poi.poifs.filesystem.POIFSFileSystem
import org.apache.poi.util.IOUtils
import org.xmlpull.v1.XmlPullParser
import java.io.File
import java.io.InputStream
import java.io.InterruptedIOException
import java.util.zip.ZipFile

data class LocalDocument(val file: File, val name: String, val kind: DocumentKind)

/** Runs on the reader's single worker. Files are private and never sent to a server. */
object DocumentReader {
    const val MAX_TEXT = 1_000_000
    const val MAX_XML_BYTES = 8L * 1024 * 1024
    const val MAX_WORD_BYTES = 12L * 1024 * 1024
    const val MAX_FILE_BYTES = 100L * 1024 * 1024
    private const val WORD_NS = "http://schemas.openxmlformats.org/wordprocessingml/2006/main"
    private const val STRICT_WORD_NS = "http://purl.oclc.org/ooxml/wordprocessingml/main"
    private var cachePrepared = false

    @Synchronized
    private fun prepareCache(cacheDir: File) {
        if (cachePrepared) return
        // Only once per process: remove reader files left behind by a killed process.
        cacheDir.listFiles { file -> file.isFile && (
            (file.name.startsWith("reader-") && file.name.endsWith(".bin")) ||
            (file.name.startsWith("reader-fonts-") && file.name.endsWith(".docx")) ||
            (file.name.startsWith("reader-sheet-") && file.name.endsWith(".pdf")) ||
            (file.name.startsWith("reader-edited-") && file.name.endsWith(".pdf")) ||
            (file.name.startsWith("reader-export-") && file.name.endsWith(".pdf"))
        ) }
            ?.forEach { it.delete() }
        cachePrepared = true
    }

    fun stage(resolver: ContentResolver, uri: Uri, cacheDir: File): LocalDocument {
        prepareCache(cacheDir)
        val name = resolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use {
            if (it.moveToFirst()) it.getString(0) else null
        } ?: "Documento"
        val file = File.createTempFile("reader-", ".bin", cacheDir)
        try {
            val stream = resolver.openInputStream(uri) ?: throw ReadingException("No se pudo leer el archivo.")
            stream.use { source ->
                file.outputStream().use { target ->
                    val buffer = ByteArray(32 * 1024)
                    var total = 0L
                    while (true) {
                        checkCancelled()
                        val count = source.read(buffer)
                        if (count < 0) break
                        total += count
                        if (total > MAX_FILE_BYTES) throw ReadingException("El archivo supera el límite de 100 MB.")
                        target.write(buffer, 0, count)
                    }
                }
            }
            val header = ByteArray(1024)
            val count = file.inputStream().use { it.read(header) }.coerceAtLeast(0)
            val kind = identify(header.copyOf(count))
            if (kind != DocumentKind.PDF && file.length() > MAX_WORD_BYTES) {
                throw ReadingException("El archivo Word supera el límite de 12 MB.")
            }
            return LocalDocument(file, name, kind)
        } catch (failure: Throwable) {
            file.delete()
            throw failure
        }
    }

    fun identify(header: ByteArray): DocumentKind = DocumentSignature.identify(header)

    fun readWord(document: LocalDocument): String = when (document.kind) {
        DocumentKind.DOCX -> readDocx(document.file)
        DocumentKind.DOC -> readDoc(document.file)
        else -> throw ReadingException("El archivo no es un documento Word.")
    }.ifBlank { "Este documento no contiene texto legible." }

    private fun readDocx(file: File): String = ZipFile(file).use { zip ->
        val entry = zip.getEntry("word/document.xml")
            ?: throw ReadingException("El archivo no contiene un documento Word válido.")
        if (entry.size > MAX_XML_BYTES) throw ReadingException("El contenido Word es demasiado grande.")
        zip.getInputStream(entry).use { parseDocx(it) }
    }

    internal fun parseDocx(input: InputStream, parser: XmlPullParser = Xml.newPullParser()): String {
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true)
        parser.setInput(LimitedInputStream(input, MAX_XML_BYTES), null)
        val result = StringBuilder()
        var inText = false
        var inCell = false
        var seenDocument = false
        while (true) {
            checkCancelled()
            val event = parser.next()
            if (event == XmlPullParser.END_DOCUMENT) break
            val wordTag = parser.namespace == WORD_NS || parser.namespace == STRICT_WORD_NS
            when (event) {
                XmlPullParser.DOCDECL -> throw ReadingException("El documento contiene XML no compatible.")
                XmlPullParser.START_TAG -> {
                    if (!seenDocument) {
                        if (!wordTag || parser.name != "document") throw ReadingException("El archivo no contiene un documento Word válido.")
                        seenDocument = true
                    }
                    if (wordTag) when (parser.name) {
                    "t" -> inText = true
                    "tc" -> inCell = true
                    "tab" -> result.append('\t')
                    "br", "cr" -> result.append('\n')
                    }
                }
                XmlPullParser.TEXT -> if (inText) result.append(parser.text)
                XmlPullParser.END_TAG -> if (wordTag) when (parser.name) {
                    "t" -> inText = false
                    "p" -> result.append(if (inCell) "\n" else "\n\n")
                    "tc" -> { result.append('\t'); inCell = false }
                    "tr" -> result.append('\n')
                }
            }
            if (result.length > MAX_TEXT) throw ReadingException("El documento contiene demasiado texto.")
        }
        if (!seenDocument) throw ReadingException("El archivo no contiene un documento Word válido.")
        return result.toString().trim()
    }

    private fun readDoc(file: File): String {
        // Bound allocations declared by untrusted binary records as well as physical file size.
        IOUtils.setByteArrayMaxOverride(16 * 1024 * 1024)
        POIFSFileSystem(file, true).use { fs ->
            if (fs.root.hasEntry("EncryptedPackage")) throw ReadingException("El documento está protegido con contraseña.")
            if (!fs.root.hasEntry("WordDocument")) throw ReadingException("El archivo no es un documento Word válido.")
            fs.createDocumentInputStream("WordDocument").use { stream ->
                val header = ByteArray(12)
                stream.readFully(header)
                if ((header[11].toInt() and 1) != 0) throw ReadingException("El documento está protegido con contraseña.")
            }
            val text = try {
                HWPFDocument(fs.root).use { document -> checkedText(document.range.text()) }
            } catch (_: OldWordFileFormatException) {
                HWPFOldDocument(fs.root).use { document -> checkedText(document.range.text()) }
            }
            return cleanWordText(Range.stripFields(text))
        }
    }

    private fun checkedText(text: String): String {
        checkCancelled()
        if (text.length > MAX_TEXT) throw ReadingException("El documento contiene demasiado texto.")
        return text
    }

    internal fun cleanWordText(text: String): String = WordText.clean(text)

    fun checkCancelled() {
        if (Thread.currentThread().isInterrupted) throw InterruptedIOException()
    }
}
