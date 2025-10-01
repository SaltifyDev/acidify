package org.ntqqrev.acidify.util

import io.ktor.client.*

actual fun createHttpClient(block: HttpClientConfig<*>.() -> Unit): HttpClient {
    return HttpClient { block() }
}