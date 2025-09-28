package org.ntqqrev.yogurt.util

import io.ktor.server.engine.*

actual fun EmbeddedServer<*, *>.configureSigIntHandler() {
    Runtime.getRuntime().addShutdownHook(Thread {
        println("SIGINT received, shutting down...")
        this.stop(gracePeriodMillis = 5000L, timeoutMillis = 10_000L)
    })
}