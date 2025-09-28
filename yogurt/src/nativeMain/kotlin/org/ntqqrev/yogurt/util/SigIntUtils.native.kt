package org.ntqqrev.yogurt.util

import io.ktor.server.engine.*
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.staticCFunction
import platform.posix.SIGINT
import platform.posix.signal

private var globalServer: EmbeddedServer<*, *>? = null

@OptIn(ExperimentalForeignApi::class)
actual fun EmbeddedServer<*, *>.configureSigIntHandler() {
    if (globalServer != null) {
        // Already configured
        return
    }
    globalServer = this
    signal(SIGINT, staticCFunction { _: Int ->
        println("SIGINT received, shutting down...")
        globalServer?.stop(gracePeriodMillis = 5000L, timeoutMillis = 10_000L)
        return@staticCFunction
    })
}