package com.example.lectorsimple

import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.io.IOException
import java.io.InterruptedIOException
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream
import javax.xml.parsers.DocumentBuilderFactory

class DocxFontCompatibilityTest {
    @get:Rule val temporary = TemporaryFolder()
    private val namespace = "http://schemas.openxmlformats.org/wordprocessingml/2006/main"

    @Test fun resolvesDeclaredAlternativeWithoutChangingPageTextImagesOrOriginal() {
        val source = fixture()
        val original = source.readBytes()
        val prepared = DocxFontCompatibility.prepare(source, temporary.root)!!
        assertArrayEquals(original, source.readBytes())
        ZipFile(prepared).use { zip ->
            for (part in listOf("word/document.xml", "word/styles.xml", "word/header1.xml")) {
                val xml = DocumentBuilderFactory.newInstance().apply { isNamespaceAware = true }
                    .newDocumentBuilder().parse(zip.getInputStream(zip.getEntry(part)))
                val fonts = xml.getElementsByTagNameNS(namespace, "rFonts")
                assertEquals("Cambria", fonts.item(0).attributes.getNamedItemNS(namespace, "ascii").nodeValue)
                assertEquals("Arial", fonts.item(1).attributes.getNamedItemNS(namespace, "ascii").nodeValue)
                assertEquals("Text unchanged", xml.getElementsByTagNameNS(namespace, "t").item(0).textContent)
                val page = xml.getElementsByTagNameNS(namespace, "pgSz").item(0)
                assertEquals("11906", page.attributes.getNamedItemNS(namespace, "w").nodeValue)
                assertEquals("16838", page.attributes.getNamedItemNS(namespace, "h").nodeValue)
            }
            assertArrayEquals(byteArrayOf(0, 1, 2, -1), zip.getInputStream(zip.getEntry("word/media/image.png")).readBytes())
        }
    }

    @Test fun usesTheDeclaredAlternativeInsteadOfHardcodingCambria() {
        val source = fixture(alternative = "Times New Roman")
        val prepared = DocxFontCompatibility.prepare(source, temporary.root)!!
        ZipFile(prepared).use { zip ->
            val text = zip.getInputStream(zip.getEntry("word/document.xml")).reader().readText()
            assertTrue(text.contains("Times New Roman"))
            assertFalse(text.contains("Cambria"))
        }
    }

    @Test fun leavesRealFontsAndDocumentsWithoutAnAlternativeUntouched() {
        assertNull(DocxFontCompatibility.prepare(fixture(primary = "Arial"), temporary.root))
        assertNull(DocxFontCompatibility.prepare(fixture(alternative = ""), temporary.root))
        val binaryDoc = temporary.newFile().apply { writeBytes(byteArrayOf(0xd0.toByte(), 0xcf.toByte(), 0x11, 0xe0.toByte())) }
        assertNull(DocxFontCompatibility.prepare(binaryDoc, temporary.root))
        assertTrue(temporary.root.listFiles()!!.none { it.name.startsWith("reader-fonts-") })
    }

    @Test fun rejectsDtdAndDeletesIncompleteCompatibilityCopy() {
        val source = fixture(xmlPrefix = "<!DOCTYPE x [<!ENTITY payload SYSTEM 'file:///not-allowed'>]>")
        try {
            DocxFontCompatibility.prepare(source, temporary.root)
            fail("Expected a rejected DTD")
        } catch (_: IOException) {
            assertTrue(temporary.root.listFiles()!!.none { it.name.startsWith("reader-fonts-") })
        }
    }

    @Test fun respectsCancellation() {
        val source = fixture()
        Thread.currentThread().interrupt()
        try {
            DocxFontCompatibility.prepare(source, temporary.root)
            fail("Expected cancellation")
        } catch (_: InterruptedIOException) {
            assertTrue(temporary.root.listFiles()!!.none { it.name.startsWith("reader-fonts-") })
        } finally {
            Thread.interrupted()
        }
    }

    private fun fixture(primary: String = "inherit", alternative: String = "Cambria", xmlPrefix: String = ""): File {
        val source = temporary.newFile()
        ZipOutputStream(source.outputStream()).use { zip ->
            fun entry(name: String, bytes: ByteArray) {
                zip.putNextEntry(ZipEntry(name)); zip.write(bytes); zip.closeEntry()
            }
            entry("word/fontTable.xml", """
                <w:fonts xmlns:w="$namespace"><w:font w:name="$primary"><w:altName w:val="$alternative"/></w:font>
                <w:font w:name="Arial"><w:altName w:val="Other Font"/></w:font></w:fonts>
            """.trimIndent().toByteArray())
            val xml = """$xmlPrefix<w:document xmlns:w="$namespace"><w:body><w:rFonts w:ascii="$primary" w:hAnsi="$primary"/>
                <w:rFonts w:ascii="Arial"/><w:t>Text unchanged</w:t><w:pgSz w:w="11906" w:h="16838"/></w:body></w:document>"""
            listOf("word/document.xml", "word/styles.xml", "word/header1.xml").forEach { entry(it, xml.toByteArray()) }
            entry("word/media/image.png", byteArrayOf(0, 1, 2, -1))
        }
        return source
    }
}
