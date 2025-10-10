package org.ntqqrev.yogurt.util

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.io.buffered
import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem
import kotlinx.io.readByteArray
import org.ntqqrev.acidify.util.createHttpClient
import kotlin.io.encoding.Base64

val httpClient = createHttpClient()

suspend fun resolveUri(uri: String): ByteArray {
    return when {
        uri.startsWith("file://") -> {
            val filePath = uri.removePrefix("file://")
            withContext(Dispatchers.Default) {
                SystemFileSystem.source(Path(filePath.decodeURLPart()))
                    .buffered()
                    .use { it.readByteArray() }
            }
        }

        uri.startsWith("http://") || uri.startsWith("https://") -> {
            httpClient.get(uri).readRawBytes()
        }

        uri.startsWith("base64://") -> {
            val base64Data = uri.removePrefix("base64://")
            Base64.decode(base64Data)
        }

        else -> throw IllegalArgumentException("Unsupported URI scheme: $uri")
    }
}