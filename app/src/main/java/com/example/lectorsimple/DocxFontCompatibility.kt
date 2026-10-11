package com.example.lectorsimple

import org.w3c.dom.Element
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.InterruptedIOException
import java.io.OutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

/** Resolves a placeholder left by HTML/Word imports using the document's own altName.
 * LibreOffice otherwise substitutes Android's default font for the literal name "inherit".
 * Real font names stay with the native font resolver. Only a private temporary copy is edited.
 */
internal object DocxFontCompatibility {
    private const val WORD_NS = "http://schemas.openxmlformats.org/wordprocessingml/2006/main"
    private const val STRICT_NS = "http://purl.oclc.org/ooxml/wordprocessingml/main"
    private const val MAX_XML_BYTES = 8L * 1024 * 1024
    private const val MAX_PACKAGE_BYTES = 100L * 1024 * 1024

    /** Returns null when no compatibility copy is required (including binary DOC). */
    fun prepare(source: File, cacheDir: File): File? {
        val signature = source.inputStream().use { it.readNBytesCompat(4) }
        if (!signature.contentEquals(byteArrayOf(0x50, 0x4b, 0x03, 0x04))) return null
        ZipFile(source).use { zip ->
            val table = zip.getEntry("word/fontTable.xml") ?: return null
            val fontTable = parseXml(zip.getInputStream(table).use { readXml(it) })
            val aliases = mutableMapOf<String, String>()
            val fonts = fontTable.getElementsByTagNameNS("*", "font")
            for (index in 0 until fonts.length) {
                val font = fonts.item(index) as Element
                val namespace = font.namespaceURI
                if (!isWordNamespace(namespace)) continue
                val name = font.getAttributeNS(namespace, "name")
                if (!name.equals("inherit", ignoreCase = true)) continue
                val alternative = font.getElementsByTagNameNS(namespace, "altName")
                    .item(0) as? Element ?: continue
                val value = alternative.getAttributeNS(namespace, "val").trim()
                if (value.isNotEmpty() && !value.equals(name, ignoreCase = true)) aliases[name] = value
            }
            if (aliases.isEmpty()) return null

            val temporary = File.createTempFile("reader-fonts-", ".docx", cacheDir)
            try {
                var changed = false
                var remaining = MAX_PACKAGE_BYTES
                ZipOutputStream(temporary.outputStream().buffered()).use { output ->
                    val entries = zip.entries()
                    while (entries.hasMoreElements()) {
                        checkCancelled()
                        val entry = entries.nextElement()
                        output.putNextEntry(ZipEntry(entry.name))
                        if (!entry.isDirectory) zip.getInputStream(entry).use { input ->
                            if (entry.name.startsWith("word/") && entry.name.endsWith(".xml")) {
                                val bytes = readXml(input)
                                remaining -= bytes.size
                                if (remaining < 0) throw IOException("El documento expandido es demasiado grande.")
                                val xml = parseXml(bytes)
                                var modified = false
                                val runs = xml.getElementsByTagNameNS("*", "rFonts")
                                for (index in 0 until runs.length) {
                                    val run = runs.item(index) as Element
                                    val namespace = run.namespaceURI
                                    if (!isWordNamespace(namespace)) continue
                                    for (attribute in arrayOf("ascii", "hAnsi", "eastAsia", "cs")) {
                                        val node = run.getAttributeNodeNS(namespace, attribute) ?: continue
                                        val replacement = aliases[node.value] ?: continue
                                        node.value = replacement
                                        modified = true
                                    }
                                }
                                if (modified) {
                                    val transformer = TransformerFactory.newInstance().newTransformer()
                                    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8")
                                    transformer.transform(DOMSource(xml), StreamResult(output))
                                    changed = true
                                } else output.write(bytes)
                            } else {
                                remaining -= copyLimited(input, output, remaining)
                            }
                        }
                        output.closeEntry()
                    }
                }
                if (changed) return temporary
                temporary.delete()
                return null
            } catch (failure: Throwable) {
                temporary.delete()
                throw failure
            }
        }
    }

    private fun isWordNamespace(namespace: String?) = namespace == WORD_NS || namespace == STRICT_NS

    private fun parseXml(bytes: ByteArray): org.w3c.dom.Document {
        checkCancelled()
        // DOCX parts must not contain a DTD. Removing NULs also detects UTF-16/32 declarations.
        if (bytes.toString(Charsets.UTF_8).replace("\u0000", "").contains("<!DOCTYPE", ignoreCase = true)) {
            throw IOException("El documento contiene una declaración XML no admitida.")
        }
        val builder = DocumentBuilderFactory.newInstance().apply { isNamespaceAware = true }.newDocumentBuilder()
        builder.setEntityResolver { _, _ -> throw IOException("No se admiten entidades XML externas.") }
        return builder.parse(ByteArrayInputStream(bytes))
    }

    private fun readXml(input: InputStream): ByteArray = ByteArrayOutputStream().use { output ->
        copyLimited(input, output, MAX_XML_BYTES)
        output.toByteArray()
    }

    private fun copyLimited(input: InputStream, output: OutputStream, limit: Long): Long {
        val buffer = ByteArray(32 * 1024)
        var total = 0L
        while (true) {
            checkCancelled()
            val count = input.read(buffer)
            if (count < 0) return total
            total += count
            if (total > limit) throw IOException("El documento expandido es demasiado grande.")
            output.write(buffer, 0, count)
        }
    }

    private fun InputStream.readNBytesCompat(count: Int): ByteArray {
        val bytes = ByteArray(count)
        var position = 0
        while (position < count) {
            val read = read(bytes, position, count - position)
            if (read < 0) break
            position += read
        }
        return bytes.copyOf(position)
    }

    private fun checkCancelled() {
        if (Thread.currentThread().isInterrupted) throw InterruptedIOException("Lectura cancelada")
    }
}
