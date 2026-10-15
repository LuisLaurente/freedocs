package com.example.lectorsimple

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.RectF
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import java.io.File
import java.io.IOException
import java.io.InterruptedIOException
import java.util.concurrent.Future
import java.util.concurrent.Semaphore
import java.util.concurrent.atomic.AtomicInteger

enum class WordViewMode { HOJA, MOVIL }

class MainActivity : Activity() {
    private lateinit var content: LinearLayout
    private lateinit var modeBar: LinearLayout
    private lateinit var btnHoja: Button
    private lateinit var btnMovil: Button
    private lateinit var title: TextView
    private lateinit var previous: Button
    private lateinit var next: Button
    private lateinit var pageLabel: TextView
    private val worker = ReaderWorker.executor
    private val generation = AtomicInteger()
    private var loading: Future<*>? = null
    private var rendering: Future<*>? = null
    private var frameVersion = 0
    // Bound decoded tiles waiting for the UI, independently of the worker speed.
    private val pendingBitmaps = Semaphore(2)
    // The worker exclusively owns the PDF session, including disposal.
    private var session: PdfSession? = null
    private var pdfView: PdfZoomView? = null
    private var wordView: WordZoomView? = null
    private var currentDocument: LocalDocument? = null
    private var currentWordText: String? = null
    private var uri: Uri? = null
    private var page = 0
    private var pageCount = 0
    private var wordMode = WordViewMode.HOJA
    private var sourceIsPdf = false
    private var pdfEditMode = false
    private val pdfEdits = PdfDocumentEdits()
    private var pendingEditedFile: File? = null
    private var extractedPdfText = ""
    private var editTextSize = 12f
    private var editColor = Color.BLACK
    private var savedHojaState: Bundle? = null
    private var savedMovilState: Bundle? = null
    private var pendingState: Bundle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        // Android displays the launch theme until the first frame; no extra Activity or delay.
        setTheme(R.style.Theme_LectorSimple)
        super.onCreate(savedInstanceState)
        buildScreen()
        val restoredUri = savedInstanceState?.getString("uri")
        if (restoredUri != null) {
            pendingState = savedInstanceState.getBundle("position")
            savedHojaState = savedInstanceState.getBundle("hoja_position")
            savedMovilState = savedInstanceState.getBundle("movil_position")
            page = savedInstanceState.getInt("page", 0)
            savedInstanceState.getString("word_mode")?.let { modeStr ->
                wordMode = try { WordViewMode.valueOf(modeStr) } catch (_: Exception) { WordViewMode.HOJA }
            }
            openDocument(Uri.parse(restoredUri), restoring = true)
        } else {
            openIncomingDocument(intent)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        openIncomingDocument(intent)
    }

    private fun openIncomingDocument(intent: Intent?) {
        if (intent?.action != Intent.ACTION_VIEW) return
        val incoming = intent.data ?: return
        openDocument(incoming)
    }

    private fun buildScreen() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.rgb(248, 249, 251))
        }
        root.setOnApplyWindowInsetsListener { view, insets ->
            if (Build.VERSION.SDK_INT >= 30) {
                val safe = insets.getInsets(WindowInsets.Type.systemBars() or WindowInsets.Type.displayCutout())
                view.setPadding(safe.left, safe.top + dp(8), safe.right, safe.bottom)
            } else {
                @Suppress("DEPRECATION")
                view.setPadding(insets.systemWindowInsetLeft, insets.systemWindowInsetTop + dp(8),
                    insets.systemWindowInsetRight, insets.systemWindowInsetBottom)
            }
            insets
        }
        title = TextView(this).apply {
            text = "Lector simple"
            textSize = 18f
            setTextColor(Color.rgb(28, 35, 48))
            maxLines = 1
            ellipsize = TextUtils.TruncateAt.END
            setPadding(dp(16), 0, dp(16), dp(4))
        }
        val open = Button(this).apply {
            text = "Abrir documento"
            setOnClickListener { chooseDocument() }
        }

        // Barra conmutadora "Hoja | Móvil" para documentos Word
        modeBar = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            visibility = View.GONE
            setPadding(dp(12), dp(4), dp(12), dp(4))
        }
        btnHoja = Button(this).apply {
            text = "Hoja (Diseño)"
            setOnClickListener { if (sourceIsPdf) setPdfEditMode(false) else switchWordMode(WordViewMode.HOJA) }
        }
        btnMovil = Button(this).apply {
            text = "Móvil (Texto)"
            setOnClickListener { if (sourceIsPdf) setPdfEditMode(true) else switchWordMode(WordViewMode.MOVIL) }
        }
        modeBar.addView(btnHoja, LinearLayout.LayoutParams(0, -2, 1f).apply { setMargins(0, 0, dp(4), 0) })
        modeBar.addView(btnMovil, LinearLayout.LayoutParams(0, -2, 1f).apply { setMargins(dp(4), 0, 0, 0) })

        content = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        root.addView(title, LinearLayout.LayoutParams(-1, -2))
        root.addView(open, LinearLayout.LayoutParams(-1, -2).apply { setMargins(dp(12), 0, dp(12), 0) })
        root.addView(modeBar, LinearLayout.LayoutParams(-1, -2))
        root.addView(content, LinearLayout.LayoutParams(-1, 0, 1f))
        setContentView(root)
        if (Build.VERSION.SDK_INT >= 30) {
            val lightBars = WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS or WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
            window.insetsController?.setSystemBarsAppearance(lightBars, lightBars)
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or
                (if (Build.VERSION.SDK_INT >= 26) View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR else 0)
        }
        root.requestApplyInsets()
        showMessage("Abre un PDF o un archivo Word (.docx o .doc).\n\nPellizca para hacer zoom.")
    }

    private fun chooseDocument() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("application/pdf", "application/msword", "application/octet-stream",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document"))
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        }
        startActivityForResult(intent, REQUEST_OPEN)
    }

    @Deprecated("Platform result API avoids an additional UI dependency.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SAVE && resultCode == RESULT_OK) {
            val target = data?.data ?: return
            val edited = pendingEditedFile ?: return
            worker.execute {
                try { contentResolver.openOutputStream(target, "wt")?.use { output -> edited.inputStream().use { it.copyTo(output) } } ?: throw IOException("No se pudo guardar en el destino seleccionado.")
                    edited.delete(); pendingEditedFile = null
                    runOnUiThread { showMessage("PDF editado guardado correctamente."); showPdfAgain() }
                } catch (failure: Exception) { edited.delete(); pendingEditedFile = null; runOnUiThread { showMessage("No se pudo guardar el PDF editado.") } }
            }
            return
        }
        if (requestCode != REQUEST_OPEN || resultCode != RESULT_OK) return
        val selected = data?.data ?: return
        try {
            contentResolver.takePersistableUriPermission(selected, data.flags and Intent.FLAG_GRANT_READ_URI_PERMISSION)
        } catch (_: SecurityException) {
            // Some providers only grant access for the current activity/session.
        }
        uri?.takeIf { it != selected }?.let { old ->
            try { contentResolver.releasePersistableUriPermission(old, Intent.FLAG_GRANT_READ_URI_PERMISSION) }
            catch (_: SecurityException) { /* No persisted grant for this provider. */ }
        }
        openDocument(selected)
    }

    private fun openDocument(selected: Uri, restoring: Boolean = false) {
        val revision = generation.incrementAndGet()
        frameVersion++
        loading?.cancel(true)
        rendering?.cancel(true)
        worker.purge()
        pdfView = null
        wordView = null
        uri = selected
        sourceIsPdf = false
        pdfEditMode = false
        pdfEdits.reset()
        extractedPdfText = ""
        if (!restoring) {
            page = 0
            pendingState = null
            savedHojaState = null
            savedMovilState = null
            wordMode = WordViewMode.HOJA
        }
        val requestedPage = page
        title.text = "Lector simple"
        modeBar.visibility = View.GONE
        showMessage("Abriendo documento…")
        loading = worker.submit {
            closeSession()
            closeCurrentDocument()
            currentWordText = null
            var local: LocalDocument? = null
            try {
                local = DocumentReader.stage(contentResolver, selected, cacheDir)
                DocumentReader.checkCancelled()
                val name = local.name
                currentDocument = local

                if (local.kind == DocumentKind.PDF) {
                    val pdf = PdfSession(local.file)
                    session = pdf
                    // PdfSession owns the file now
                    currentDocument = null
                    val count = pdf.pageCount
                    val selectedPage = requestedPage.coerceIn(0, count - 1)
                    val size = pdf.pageSize(selectedPage)
                    runOnUiThread {
                        if (generation.get() == revision && !isDestroyed) {
                            title.text = name
                            page = selectedPage
                            pageCount = count
                            sourceIsPdf = true
                            modeBar.visibility = View.VISIBLE
                            updateModeButtons()
                            extractedPdfText = try { PdfTextExtractor.extract(pdf.file).lineSequence().firstOrNull { it.isNotBlank() }.orEmpty() } catch (_: Exception) { "" }
                            showPdf(size, pendingState)
                            pendingState = null
                        }
                    }
                } else {
                    // Documento Word (.doc o .docx)
                    runOnUiThread {
                        if (generation.get() == revision && !isDestroyed) {
                            title.text = name
                            modeBar.visibility = View.VISIBLE
                            updateModeButtons()
                        }
                    }
                    if (wordMode == WordViewMode.HOJA) {
                        displayWordAsHoja(local, requestedPage, revision, name, pendingState ?: savedHojaState)
                        pendingState = null
                    } else {
                        displayWordAsMovil(local, revision, name, pendingState ?: savedMovilState)
                        pendingState = null
                    }
                }
            } catch (failure: Exception) {
                closeSession()
                closeCurrentDocument()
                reportFailure(failure, revision)
            } catch (_: OutOfMemoryError) {
                closeSession()
                closeCurrentDocument()
                reportFailure(ReadingException("El documento necesita demasiada memoria."), revision)
            }
        }
    }

    private fun displayWordAsHoja(local: LocalDocument, requestedPage: Int, revision: Int, docTitle: String, stateToRestore: Bundle?) {
        val sheetTempPdf = File.createTempFile("reader-sheet-", ".pdf", cacheDir)
        try {
            LibreOfficeEngine.convertWordToPdf(this@MainActivity, local.file, sheetTempPdf)
            DocumentReader.checkCancelled()
            val pdf = PdfSession(sheetTempPdf)
            session = pdf
            val count = pdf.pageCount
            val selectedPage = requestedPage.coerceIn(0, count - 1)
            val size = pdf.pageSize(selectedPage)
            runOnUiThread {
                if (generation.get() == revision && !isDestroyed) {
                    title.text = docTitle
                    page = selectedPage
                    pageCount = count
                    modeBar.visibility = View.VISIBLE
                    updateModeButtons()
                    showPdf(size, stateToRestore)
                }
            }
        } catch (t: Throwable) {
            sheetTempPdf.delete()
            throw t
        }
    }

    private fun displayWordAsMovil(local: LocalDocument, revision: Int, docTitle: String, stateToRestore: Bundle?) {
        val text = currentWordText ?: DocumentReader.readWord(local).also { currentWordText = it }
        runOnUiThread {
            if (generation.get() == revision && !isDestroyed) {
                title.text = docTitle
                modeBar.visibility = View.VISIBLE
                updateModeButtons()
                content.removeAllViews()
                wordView = WordZoomView(this@MainActivity, text, stateToRestore)
                content.addView(wordView, LinearLayout.LayoutParams(-1, -1))
            }
        }
    }

    private fun switchWordMode(targetMode: WordViewMode) {
        if (wordMode == targetMode) return
        val doc = currentDocument ?: return
        val revision = generation.incrementAndGet()
        frameVersion++
        loading?.cancel(true)
        rendering?.cancel(true)
        worker.purge()

        if (wordMode == WordViewMode.HOJA) {
            savedHojaState = pdfView?.savePosition()
        } else {
            savedMovilState = wordView?.savePosition()
        }
        wordMode = targetMode
        updateModeButtons()

        showMessage(if (targetMode == WordViewMode.HOJA) "Generando vista de hoja…" else "Cargando texto…")
        loading = worker.submit {
            try {
                if (targetMode == WordViewMode.HOJA) {
                    closeSession()
                    displayWordAsHoja(doc, page, revision, doc.name, savedHojaState)
                } else {
                    displayWordAsMovil(doc, revision, doc.name, savedMovilState)
                }
            } catch (failure: Exception) {
                reportFailure(failure, revision)
            }
        }
    }

    private fun updateModeButtons() {
        if (sourceIsPdf) {
            btnHoja.text = "Lectura"; btnMovil.text = "Editar PDF"
            val selected = if (pdfEditMode) btnMovil else btnHoja
            val other = if (pdfEditMode) btnHoja else btnMovil
            selected.setTextColor(Color.WHITE); selected.setBackgroundColor(Color.rgb(35, 75, 140)); selected.setTypeface(null, Typeface.BOLD)
            other.setTextColor(Color.DKGRAY); other.setBackgroundColor(Color.rgb(220, 224, 232)); other.setTypeface(null, Typeface.NORMAL)
            return
        }
        btnHoja.text = "Hoja (Diseño)"; btnMovil.text = "Móvil (Texto)"
        if (wordMode == WordViewMode.HOJA) {
            btnHoja.setTextColor(Color.WHITE)
            btnHoja.setBackgroundColor(Color.rgb(35, 75, 140))
            btnHoja.setTypeface(null, Typeface.BOLD)
            btnMovil.setTextColor(Color.DKGRAY)
            btnMovil.setBackgroundColor(Color.rgb(220, 224, 232))
            btnMovil.setTypeface(null, Typeface.NORMAL)
        } else {
            btnMovil.setTextColor(Color.WHITE)
            btnMovil.setBackgroundColor(Color.rgb(35, 75, 140))
            btnMovil.setTypeface(null, Typeface.BOLD)
            btnHoja.setTextColor(Color.DKGRAY)
            btnHoja.setBackgroundColor(Color.rgb(220, 224, 232))
            btnHoja.setTypeface(null, Typeface.NORMAL)
        }
    }

    private fun showPdf(size: Pair<Int, Int>, restoreState: Bundle? = null) {
        content.removeAllViews()
        val controls = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
        previous = Button(this).apply { text = "Anterior"; setOnClickListener { changePage(page - 1) } }
        next = Button(this).apply { text = "Siguiente"; setOnClickListener { changePage(page + 1) } }
        pageLabel = TextView(this).apply { gravity = Gravity.CENTER; setTextColor(Color.DKGRAY); textSize = 14f }
        controls.addView(previous, LinearLayout.LayoutParams(0, -2, 1f))
        controls.addView(pageLabel, LinearLayout.LayoutParams(0, -2, 1f))
        controls.addView(next, LinearLayout.LayoutParams(0, -2, 1f))
        pdfView = PdfZoomView(this, ::renderFrame)
        pdfView?.documentEdits = pdfEdits
        pdfView?.editMode = pdfEditMode
        pdfView?.onExistingText = { extractedPdfText.lineSequence().filter { it.isNotBlank() }.take(1).joinToString(" ") }
        pdfView?.onEditText = { bounds, background, text -> showTextEditor(bounds, background, text, true) }
        pdfView?.onEditTap = { tool, x, y -> if (tool == PdfTool.ADD_TEXT) showTextEditor(RectF(x, y, x + 190f, y + 48f), Color.WHITE, "", false) }
        content.addView(pdfView, LinearLayout.LayoutParams(-1, 0, 1f))
        if (sourceIsPdf) content.addView(buildPdfEditToolbar(), LinearLayout.LayoutParams(-1, -2))
        content.addView(controls, LinearLayout.LayoutParams(-1, -2))
        pdfView?.showPage(page, size, restoreState)
        updateControls()
    }

    private fun buildPdfEditToolbar(): View {
        val scroll = HorizontalScrollView(this).apply { isHorizontalScrollBarEnabled = false }
        val bar = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; visibility = if (pdfEditMode) View.VISIBLE else View.GONE }
        fun tool(label: String, tool: PdfTool) = Button(this).apply { text = label; setOnClickListener { pdfView?.activeTool = tool } }
        bar.addView(tool("Editar texto", PdfTool.REPLACE_TEXT)); bar.addView(tool("+ Texto", PdfTool.ADD_TEXT)); bar.addView(tool("Mover", PdfTool.MOVE))
        bar.addView(tool("Firmar", PdfTool.INK)); bar.addView(tool("Resaltar", PdfTool.HIGHLIGHT))
        bar.addView(Button(this).apply { text = "Borrar"; setOnClickListener { pdfEdits.clearPage(page); pdfView?.invalidate() } })
        bar.addView(Button(this).apply { text = "Deshacer"; setOnClickListener { pdfEdits.undo(); pdfView?.invalidate() } })
        bar.addView(Button(this).apply { text = "Guardar PDF"; setOnClickListener { saveEditedPdf() } })
        scroll.addView(bar); return scroll
    }

    private fun setPdfEditMode(enabled: Boolean) {
        pdfEditMode = enabled
        updateModeButtons()
        pdfView?.editMode = enabled
        (content.getChildAt(1) as? HorizontalScrollView)?.getChildAt(0)?.visibility = if (enabled) View.VISIBLE else View.GONE
    }

    private fun showTextEditor(bounds: RectF, background: Int, initial: String, cover: Boolean) {
        val input = EditText(this).apply { setText(initial); minLines = 1; maxLines = 6; textSize = editTextSize }
        val coverCheck = CheckBox(this).apply { text = "Cubrir el texto original"; isChecked = cover }
        val sizeRow = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
        val preview = TextView(this).apply { text = "${editTextSize.toInt()} sp"; setPadding(dp(12), 0, dp(12), 0) }
        sizeRow.addView(Button(this).apply { text = "A−"; setOnClickListener { editTextSize = (editTextSize - 1).coerceAtLeast(6f); preview.text = "${editTextSize.toInt()} sp"; input.textSize = editTextSize } })
        sizeRow.addView(preview); sizeRow.addView(Button(this).apply { text = "A+"; setOnClickListener { editTextSize = (editTextSize + 1).coerceAtMost(72f); preview.text = "${editTextSize.toInt()} sp"; input.textSize = editTextSize } })
        val colors = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        listOf("Negro" to Color.BLACK, "Azul" to Color.BLUE, "Rojo" to Color.RED).forEach { (label, color) -> colors.addView(Button(this).apply { text = label; setOnClickListener { editColor = color } }) }
        val body = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setPadding(dp(20), dp(8), dp(20), dp(4)); addView(input); addView(sizeRow); addView(colors); if (cover) addView(coverCheck) }
        android.app.AlertDialog.Builder(this).setTitle(if (cover) "Editar texto" else "Añadir texto").setView(body)
            .setNegativeButton("Cancelar", null).setPositiveButton("Aplicar") { _, _ ->
                if (cover && !coverCheck.isChecked) return@setPositiveButton
                pdfEdits.add(PdfTextEdit(System.nanoTime(), page, RectF(bounds), input.text.toString(), editTextSize, editColor, background, coverCheck.isChecked))
                pdfView?.invalidate()
            }.show()
    }

    private fun saveEditedPdf() {
        val active = session ?: return
        val temp = File.createTempFile("reader-edited-", ".pdf", cacheDir)
        val revision = generation.get()
        showMessage("Guardando PDF editado…")
        worker.execute {
            try {
                PdfSaver.saveEditedPdf(active, pdfEdits, temp)
                val source = uri
                val savedDirectly = try {
                    if (source == null) false else contentResolver.openOutputStream(source, "wt")?.use { output -> temp.inputStream().use { it.copyTo(output) }; true } ?: false
                } catch (_: Exception) { false }
                if (savedDirectly) { temp.delete(); runOnUiThread { if (revision == generation.get()) { showMessage("PDF editado guardado en el documento original."); showPdfAgain() } } }
                else runOnUiThread {
                    if (revision != generation.get() || isDestroyed) { temp.delete(); return@runOnUiThread }
                    pendingEditedFile = temp
                    val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply { addCategory(Intent.CATEGORY_OPENABLE); type = "application/pdf"; putExtra(Intent.EXTRA_TITLE, "documento-editado.pdf") }
                    startActivityForResult(intent, REQUEST_SAVE)
                }
            } catch (failure: Exception) { temp.delete(); runOnUiThread { if (revision == generation.get()) { showMessage("No se pudo guardar el PDF editado."); showPdfAgain() } } }
        }
    }

    private fun showPdfAgain() {
        val active = session ?: return
        worker.execute { try { val size = active.pageSize(page); runOnUiThread { if (!isDestroyed) showPdf(size, pdfView?.savePosition()) } } catch (_: Exception) {} }
    }

    private fun changePage(index: Int) {
        if (index !in 0 until pageCount) return
        page = index
        frameVersion++
        rendering?.cancel(true)
        previous.isEnabled = false
        next.isEnabled = false
        val revision = generation.get()
        val version = frameVersion
        worker.submit {
            try {
                val size = session?.pageSize(index) ?: return@submit
                runOnUiThread {
                    if (revision == generation.get() && version == frameVersion && !isDestroyed) {
                        pdfView?.showPage(index, size)
                        updateControls()
                    }
                }
            } catch (failure: Exception) { reportFailure(failure, revision) }
        }
    }

    private fun updateControls() {
        if (::previous.isInitialized && ::next.isInitialized && ::pageLabel.isInitialized) {
            previous.isEnabled = page > 0
            next.isEnabled = page < pageCount - 1
            pageLabel.text = "${page + 1} / $pageCount"
        }
    }

    private fun renderFrame(viewport: PdfViewport) {
        // Ignore delayed draws from the previous page while the worker loads the next one.
        if (viewport.page != page) return
        val revision = generation.get()
        val version = frameVersion
        val targetView = pdfView ?: return
        rendering?.cancel(true)
        worker.purge()
        rendering = worker.submit {
            try {
                session?.render(viewport) { image ->
                    try {
                        pendingBitmaps.acquire()
                    } catch (interrupted: InterruptedException) {
                        image.bitmap.recycle()
                        throw InterruptedIOException()
                    }
                    runOnUiThread {
                        try {
                            if (revision == generation.get() && version == frameVersion && !isDestroyed && pdfView === targetView) {
                                targetView.displayFrame(image)
                            } else image.bitmap.recycle()
                        } finally { pendingBitmaps.release() }
                    }
                }
            } catch (failure: Exception) {
                if (failure !is InterruptedIOException && !Thread.currentThread().isInterrupted) reportFailure(failure, revision, version)
            } catch (_: OutOfMemoryError) {
                reportFailure(ReadingException("No hay suficiente memoria para mostrar esta página."), revision, version)
            }
        }
    }

    private fun reportFailure(failure: Exception, revision: Int, version: Int? = null) {
        if (failure is InterruptedIOException || Thread.currentThread().isInterrupted) return
        val message = when {
            failure is ReadingException -> failure.message!!
            failure is SecurityException || failure.javaClass.simpleName.contains("Encrypted") -> "No se pudo abrir el documento. Comprueba sus permisos o si está protegido con contraseña."
            else -> "No se pudo abrir el documento. Puede estar dañado o usar un formato no compatible."
        }
        runOnUiThread {
            if (revision == generation.get() && !isDestroyed && (version == null || version == frameVersion)) {
                generation.incrementAndGet()
                frameVersion++
                rendering?.cancel(true)
                worker.execute {
                    closeSession()
                    closeCurrentDocument()
                }
                pdfView = null
                wordView = null
                modeBar.visibility = View.GONE
                showMessage(message)
            }
        }
    }

    private fun showMessage(message: String) {
        content.removeAllViews()
        content.addView(TextView(this).apply {
            text = message
            textSize = 16f
            gravity = Gravity.CENTER
            setTextColor(Color.DKGRAY)
            setPadding(dp(16), dp(16), dp(16), dp(16))
        }, LinearLayout.LayoutParams(-1, -1))
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("uri", uri?.toString())
        outState.putInt("page", page)
        outState.putString("word_mode", wordMode.name)
        val currentPosition = if (pdfView != null && ::previous.isInitialized && !previous.isEnabled && !next.isEnabled && pageCount > 1)
            null else pdfView?.savePosition() ?: wordView?.savePosition() ?: pendingState
        outState.putBundle("position", currentPosition)
        outState.putBundle("hoja_position", if (wordMode == WordViewMode.HOJA) pdfView?.savePosition() else savedHojaState)
        outState.putBundle("movil_position", if (wordMode == WordViewMode.MOVIL) wordView?.savePosition() else savedMovilState)
    }

    private fun closeSession() {
        val closing = session
        session = null
        closing?.close()
    }

    private fun closeCurrentDocument() {
        val doc = currentDocument
        currentDocument = null
        doc?.file?.delete()
    }

    override fun onDestroy() {
        generation.incrementAndGet()
        loading?.cancel(true)
        rendering?.cancel(true)
        worker.purge()
        worker.execute {
            closeSession()
            closeCurrentDocument()
        }
        super.onDestroy()
    }

    private fun dp(value: Int) = (value * resources.displayMetrics.density).toInt()
    companion object { private const val REQUEST_OPEN = 1001; private const val REQUEST_SAVE = 1002 }
}
