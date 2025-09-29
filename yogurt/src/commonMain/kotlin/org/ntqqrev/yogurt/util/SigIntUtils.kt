package org.ntqqrev.yogurt.util

import io.ktor.server.engine.*

fun <TEngine : ApplicationEngine, TConfiguration : ApplicationEngine.Configuration> EmbeddedServer<TEngine, TConfiguration>.addSigIntHandler(): EmbeddedServer<TEngine, TConfiguration> {
    configureSigIntHandler()
    return this
}

expect fun EmbeddedServer<*, *>.configureSigIntHandler()