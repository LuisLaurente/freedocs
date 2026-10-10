package com.example.lectorsimple

import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/** Shared across rotations so two activities never extract/render documents concurrently. */
internal object ReaderWorker {
    val executor = ThreadPoolExecutor(1, 1, 30, TimeUnit.SECONDS, LinkedBlockingQueue()) {
        task -> Thread(task, "document-reader")
    }.apply { allowCoreThreadTimeOut(true) }
}
