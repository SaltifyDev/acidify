package org.ntqqrev.yogurt.util

import io.ktor.server.engine.EmbeddedServer

expect fun EmbeddedServer<*, *>.configureSigIntHandler()