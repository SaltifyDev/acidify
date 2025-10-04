package org.ntqqrev.acidify.internal.util

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import org.ntqqrev.acidify.util.createHttpClient

private val httpClient = createHttpClient { }

actual fun flashTransferPostWithBlock(url: String, body: ByteArray): ByteArray = runBlocking {
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

actual fun highwayPostWithBlock(url: String, body: ByteArray): ByteArray = runBlocking {
    val response = httpClient.post(url) {
        headers {
            append(HttpHeaders.Connection, "Keep-Alive")
            append(HttpHeaders.AcceptEncoding, "identity")
            append(HttpHeaders.UserAgent, "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.2)")
            append(HttpHeaders.ContentLength, body.size.toString())
        }
        setBody(body)
    }
    response.readRawBytes()
}