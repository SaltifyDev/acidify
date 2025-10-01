package org.ntqqrev.acidify.util

import io.ktor.client.*
import io.ktor.client.engine.curl.*

actual fun createHttpClient(block: HttpClientConfig<*>.() -> Unit): HttpClient {
    return HttpClient(Curl) {
        engine {
            sslVerify = false
        }
        block()
    }
}