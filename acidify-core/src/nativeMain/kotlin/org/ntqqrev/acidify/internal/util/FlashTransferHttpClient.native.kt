package org.ntqqrev.acidify.internal.util

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import org.ntqqrev.acidify.util.createHttpClient

private val httpClient = createHttpClient { }

actual fun postWithBlock(url: String, body: ByteArray): ByteArray = runBlocking {
    val response = httpClient.post(url) {
        headers {
            append(HttpHeaders.Accept, "*/*")
            append(HttpHeaders.Expect, "100-continue")
            append(HttpHeaders.Connection, "Keep-Alive")
            append(HttpHeaders.AcceptEncoding, "gzip")
        }
        setBody(body)
    }
    response.readRawBytes()
}