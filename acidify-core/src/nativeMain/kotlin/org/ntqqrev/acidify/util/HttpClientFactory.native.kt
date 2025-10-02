package org.ntqqrev.acidify.util

import io.ktor.client.*
import io.ktor.client.engine.curl.*
import kotlin.experimental.ExperimentalNativeApi

@OptIn(ExperimentalNativeApi::class)
actual fun createHttpClient(block: HttpClientConfig<*>.() -> Unit): HttpClient {
    return HttpClient(Curl) {
        engine {
            sslVerify = when (Platform.osFamily) {
                OsFamily.WINDOWS -> false
                else -> true
            }
        }
        block()
    }
}