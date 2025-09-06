package org.ntqqrev.acidify.util

import co.touchlab.kermit.Logger
import co.touchlab.kermit.loggerConfigInit
import co.touchlab.kermit.platformLogWriter
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.ProxyBuilder
import io.ktor.client.engine.http
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.ntqqrev.acidify.common.AppInfo
import org.ntqqrev.acidify.common.SignProvider
import org.ntqqrev.acidify.internal.util.fromHex
import org.ntqqrev.acidify.internal.util.toHex

class UrlSignProvider(val url: String, val httpProxy: String? = null) : SignProvider {
    private val logger = Logger(
        loggerConfigInit(platformLogWriter()),
        "UrlSignProvider"
    )

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
        engine {
            if (!httpProxy.isNullOrEmpty()) {
                proxy = ProxyBuilder.http(httpProxy)
            }
        }
    }

    override suspend fun sign(cmd: String, seq: Int, src: ByteArray): SignProvider.Result {
        logger.d { "Requesting sign for cmd=$cmd, seq=$seq, src=${src.toHex()}" }
        val value = client.post(url) {
            contentType(ContentType.Application.Json)
            setBody(UrlSignRequest(cmd, seq, src.toHex()))
        }.body<UrlSignResponse>().value
        logger.d { "Received sign result" }
        return SignProvider.Result(value.sign.fromHex(), value.token.fromHex(), value.extra.fromHex())
    }

    fun getAppInfo(): AppInfo? {
        return runBlocking {
            val response = client.get("$url/appinfo")
            if (response.status == HttpStatusCode.OK) {
                return@runBlocking response.body<AppInfo>()
            } else {
                return@runBlocking null
            }
        }
    }
}

@Serializable
private data class UrlSignRequest(
    val cmd: String,
    val seq: Int,
    val src: String
)

@Serializable
private data class UrlSignResponse(
    val platform: String,
    val version: String,
    val value: UrlSignValue
)

@Serializable
private data class UrlSignValue(
    val sign: String,
    val token: String,
    val extra: String
)