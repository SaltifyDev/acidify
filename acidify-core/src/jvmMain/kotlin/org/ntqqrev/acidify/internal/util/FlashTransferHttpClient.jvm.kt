package org.ntqqrev.acidify.internal.util

import net.covers1624.curl4j.CABundle
import net.covers1624.curl4j.httpapi.Curl4jHttpEngine
import net.covers1624.quack.net.httpapi.HeaderList
import net.covers1624.quack.net.httpapi.WebBody

val engine = Curl4jHttpEngine(CABundle.builtIn())

actual fun postWithBlock(url: String, body: ByteArray): ByteArray {
    val request = engine.newRequest()
        .method("POST", WebBody.BytesBody(body, null))
        .url(url)
        .headers(
            HeaderList().apply {
                add("Accept", "*/*")
                add("Expect", "100-continue")
                add("Connection", "Keep-Alive")
                add("Accept-Encoding", "gzip")
            }
        )
    request.execute().use {
        if (it.statusCode() !in 200..299) {
            throw IllegalStateException("HTTP request failed with status code ${it.statusCode()}")
        }
        return it.body()!!.asBytes()
    }
}