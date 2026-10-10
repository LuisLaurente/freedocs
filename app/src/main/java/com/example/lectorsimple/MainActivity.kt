package com.example.lectorsimple

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import java.io.InterruptedIOException
import java.util.concurrent.Future
import java.util.concurrent.Semaphore
import java.util.concurrent.atomic.AtomicInteger

class MainActivity : Activity() {
    private lateinit var content: LinearLayout
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
    private var uri: Uri? = null
    private var page = 0
    private var pageCount = 0
    private var pendingState: Bundle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        // Android displays the launch theme until the first frame; no extra Activity or delay.
        setTheme(R.style.Theme_LectorSimple)
        super.onCreate(savedInstanceState)
        buildScreen()
        savedInstanceState?.getString("uri")?.let {
            pendingState = savedInstanceState.getBundle("position")
            page = savedInstanceState.getInt("page", 0)
            openDocument(Uri.parse(it), restoring = true)
        }
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
        content = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
        root.addView(title, LinearLayout.LayoutParams(-1, -2))
        root.addView(open, LinearLayout.LayoutParams(-1, -2).apply { setMargins(dp(12), 0, dp(12), 0) })
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
        if (!restoring) { page = 0; pendingState = null }
        val requestedPage = page
        title.text = "Lector simple"
        showMessage("Abriendo documento…")
        loading = worker.submit {
            closeSession()
            var local: LocalDocument? = null
            try {
                local = DocumentReader.stage(contentResolver, selected, cacheDir)
                DocumentReader.checkCancelled()
                val name = local.name
                if (local.kind == DocumentKind.PDF) {
                    val pdf = PdfSession(local.file)
                    session = pdf
                    local = null // The PdfSession owns the temporary file now.
                    val count = pdf.pageCount
                    val selectedPage = requestedPage.coerceIn(0, count - 1)
                    val size = pdf.pageSize(selectedPage)
                    runOnUiThread {
                        if (generation.get() == revision && !isDestroyed) {
                            title.text = name
                            page = selectedPage
                            pageCount = count
                            showPdf(size)
                        }
                    }
                } else {
                    val text = DocumentReader.readWord(local)
                    runOnUiThread {
                        if (generation.get() == revision && !isDestroyed) {
                            title.text = name
                            content.removeAllViews()
                            wordView = WordZoomView(this, text, pendingState)
                            pendingState = null
                            content.addView(wordView, LinearLayout.LayoutParams(-1, -1))
                        }
                    }
                }
            } catch (failure: Exception) {
                closeSession()
                reportFailure(failure, revision)
            } catch (_: OutOfMemoryError) {
                closeSession()
                reportFailure(ReadingException("El documento necesita demasiada memoria."), revision)
            } finally {
                local?.file?.delete()
            }
        }
    }

    private fun showPdf(size: Pair<Int, Int>) {
        content.removeAllViews()
        val controls = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL; gravity = Gravity.CENTER_VERTICAL }
        previous = Button(this).apply { text = "Anterior"; setOnClickListener { changePage(page - 1) } }
        next = Button(this).apply { text = "Siguiente"; setOnClickListener { changePage(page + 1) } }
        pageLabel = TextView(this).apply { gravity = Gravity.CENTER; setTextColor(Color.DKGRAY); textSize = 14f }
        controls.addView(previous, LinearLayout.LayoutParams(0, -2, 1f))
        controls.addView(pageLabel, LinearLayout.LayoutParams(0, -2, 1f))
        controls.addView(next, LinearLayout.LayoutParams(0, -2, 1f))
        content.addView(controls, LinearLayout.LayoutParams(-1, -2))
        pdfView = PdfZoomView(this, ::renderFrame)
        content.addView(pdfView, LinearLayout.LayoutParams(-1, 0, 1f))
        pdfView?.showPage(page, size, pendingState)
        pendingState = null
        updateControls()
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
        previous.isEnabled = page > 0
        next.isEnabled = page < pageCount - 1
        pageLabel.text = "${page + 1} / $pageCount"
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
                worker.execute { closeSession() }
                pdfView = null
                wordView = null
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
        outState.putBundle("position", if (pdfView != null && ::previous.isInitialized && !previous.isEnabled && !next.isEnabled && pageCount > 1)
            null else pdfView?.savePosition() ?: wordView?.savePosition() ?: pendingState)
    }

    private fun closeSession() {
        val closing = session
        session = null
        closing?.close()
    }

    override fun onDestroy() {
        generation.incrementAndGet()
        loading?.cancel(true)
        rendering?.cancel(true)
        worker.purge()
        worker.execute { closeSession() }
        super.onDestroy()
    }

    private fun dp(value: Int) = (value * resources.displayMetrics.density).toInt()
    companion object { private const val REQUEST_OPEN = 1001 }
}
