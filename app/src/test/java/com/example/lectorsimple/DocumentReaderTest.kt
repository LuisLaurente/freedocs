package com.example.lectorsimple

import org.junit.Assert.*
import org.junit.Test
import org.kxml2.io.KXmlParser
import org.apache.poi.poifs.filesystem.POIFSFileSystem
import java.io.ByteArrayInputStream
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class DocumentReaderTest {
    private fun parse(xml: String) = DocumentReader.parseDocx(xml.byteInputStream(), KXmlParser())
    private fun word(body: String, namespace: String = "http://schemas.openxmlformats.org/wordprocessingml/2006/main") =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?><w:document xmlns:w=\"$namespace\"><w:body>$body</w:body></w:document>"

    @Test fun identifiesRealSignaturesWithoutFilename() {
        assertEquals(DocumentKind.PDF, DocumentReader.identify("\n%PDF-1.7\n".toByteArray()))
        assertEquals(DocumentKind.DOCX, DocumentReader.identify(byteArrayOf(0x50, 0x4b, 3, 4)))
        assertEquals(DocumentKind.DOC, DocumentReader.identify(byteArrayOf(0xd0.toByte(), 0xcf.toByte(), 0x11, 0xe0.toByte(), 0xa1.toByte(), 0xb1.toByte(), 0x1a, 0xe1.toByte())))
        assertThrows(ReadingException::class.java) { DocumentReader.identify("not a Word file".toByteArray()) }
    }

    @Test fun readsAccentsSplitRunsAndParagraphs() {
        assertEquals("Diseño y música\n\nSegundo párrafo", parse(word(
            "<w:p><w:r><w:t>Diseño </w:t></w:r><w:r><w:t>y música</w:t></w:r></w:p><w:p><w:r><w:t>Segundo párrafo</w:t></w:r></w:p>")))
    }

    @Test fun acceptsStrictWordNamespaceAndPreservesTabsAndBreaks() {
        assertEquals("A\tB\nC", parse(word("<w:p><w:r><w:t>A</w:t><w:tab/><w:t>B</w:t><w:br/><w:t>C</w:t></w:r></w:p>",
            "http://purl.oclc.org/ooxml/wordprocessingml/main")))
    }

    @Test fun rejectsNonWordXmlAndBrokenXml() {
        assertThrows(ReadingException::class.java) { parse("<document><t>fake</t></document>") }
        assertThrows(Exception::class.java) { parse(word("<w:p><w:t>unfinished")) }
    }

    @Test fun refusesExcessiveTextInsteadOfTruncatingSilently() {
        assertThrows(ReadingException::class.java) {
            parse(word("<w:p><w:r><w:t>${"a".repeat(DocumentReader.MAX_TEXT + 1)}</w:t></w:r></w:p>"))
        }
    }

    @Test fun rejectsOversizedDecompressedInputEvenWhenReadInSmallBlocks() {
        val input = LimitedInputStream(ByteArrayInputStream(ByteArray(65)), 64)
        val buffer = ByteArray(8)
        repeat(8) { assertEquals(8, input.read(buffer)) }
        assertThrows(ReadingException::class.java) { input.read(buffer) }
    }

    @Test fun cleansBinaryWordControlCharacters() {
        assertEquals("Diseño\nCelda\tOtra\nFinal", DocumentReader.cleanWordText("Diseño\rCelda\u0007Otra\u000bFinal\u0000"))
    }

    @Test fun largeWordBlocksPreserveAllTextIncludingEmoji() {
        val text = "a".repeat(2999) + "🎸" + " texto\n".repeat(2000)
        val blocks = WordText.splitBlocks(text)
        assertEquals(text, blocks.joinToString(""))
        assertTrue(blocks.all { it.length <= 3000 })
        assertTrue(blocks.none { it.lastOrNull()?.isHighSurrogate() == true })
    }

    @Test fun rejectsProtectedBinaryWordBeforeParsing() {
        val file = File.createTempFile("reader-test-", ".doc")
        try {
            POIFSFileSystem().use { fs ->
                val header = ByteArray(12).apply { this[11] = 1 }
                fs.createDocument(ByteArrayInputStream(header), "WordDocument")
                file.outputStream().use { fs.writeFilesystem(it) }
            }
            val error = assertThrows(ReadingException::class.java) {
                DocumentReader.readWord(LocalDocument(file, "protected.doc", DocumentKind.DOC))
            }
            assertTrue(error.message!!.contains("contraseña"))
        } finally { file.delete() }
    }

    @Test fun rejectsOleContainersThatAreNotWordDocuments() {
        val file = File.createTempFile("reader-test-", ".doc")
        try {
            POIFSFileSystem().use { fs ->
                fs.createDocument("hello".byteInputStream(), "OtherStream")
                file.outputStream().use { fs.writeFilesystem(it) }
            }
            assertThrows(ReadingException::class.java) {
                DocumentReader.readWord(LocalDocument(file, "fake.doc", DocumentKind.DOC))
            }
        } finally { file.delete() }
    }

    @Test fun rejectsZipContainersThatAreNotWordDocuments() {
        val file = File.createTempFile("reader-test-", ".docx")
        try {
            ZipOutputStream(file.outputStream()).use {
                it.putNextEntry(ZipEntry("notes.txt"))
                it.write("hello".toByteArray())
                it.closeEntry()
            }
            assertThrows(ReadingException::class.java) {
                DocumentReader.readWord(LocalDocument(file, "fake.docx", DocumentKind.DOCX))
            }
        } finally { file.delete() }
    }
}
