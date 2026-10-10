package com.example.lectorsimple

import java.io.InputStream

enum class DocumentKind { PDF, DOCX, DOC }
class ReadingException(message: String) : Exception(message)

internal object DocumentSignature {
    fun identify(header: ByteArray): DocumentKind {
        val ole = byteArrayOf(0xd0.toByte(), 0xcf.toByte(), 0x11, 0xe0.toByte(), 0xa1.toByte(), 0xb1.toByte(), 0x1a, 0xe1.toByte())
        if (header.size >= 8 && header.copyOfRange(0, 8).contentEquals(ole)) return DocumentKind.DOC
        if (header.size >= 4 && header[0] == 0x50.toByte() && header[1] == 0x4b.toByte() &&
            header[2] == 3.toByte() && header[3] == 4.toByte()) return DocumentKind.DOCX
        if (header.toString(Charsets.ISO_8859_1).contains("%PDF-")) return DocumentKind.PDF
        throw ReadingException("Elige un PDF o un archivo Word .docx o .doc.")
    }
}

internal class LimitedInputStream(private val input: InputStream, private val limit: Long) : InputStream() {
    private var consumed = 0L
    private fun count(amount: Int): Int {
        if (amount > 0) {
            consumed += amount
            if (consumed > limit) throw ReadingException("El contenido Word es demasiado grande.")
        }
        return amount
    }
    override fun read(): Int {
        val value = input.read()
        if (value >= 0) count(1)
        return value
    }
    override fun read(buffer: ByteArray, offset: Int, length: Int): Int = count(input.read(buffer, offset, length))
    override fun close() = input.close()
}

internal object WordText {
    fun clean(text: String): String = text.replace("\r\n", "\n")
        .replace('\r', '\n').replace('\u0007', '\t').replace('\u000b', '\n')
        .replace('\u000c', '\n').filter { it >= ' ' || it == '\n' || it == '\t' }.trim()

    /** Prefer paragraph boundaries and keep UTF-16 surrogate pairs together. */
    fun splitBlocks(text: String): List<String> {
        if (text.isEmpty()) return listOf("")
        val result = ArrayList<String>()
        var start = 0
        while (start < text.length) {
            var end = (start + 3000).coerceAtMost(text.length)
            if (end < text.length) {
                val paragraph = text.lastIndexOf('\n', end - 1)
                if (paragraph > start + 1500) end = paragraph + 1
                if (end > start && text[end - 1].isHighSurrogate()) end--
            }
            result.add(text.substring(start, end))
            start = end
        }
        return result
    }
}
