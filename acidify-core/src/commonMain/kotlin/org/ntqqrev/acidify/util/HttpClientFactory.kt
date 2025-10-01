package org.ntqqrev.acidify.util

import io.ktor.client.*

expect fun createHttpClient(block: HttpClientConfig<*>.() -> Unit = {}): HttpClient