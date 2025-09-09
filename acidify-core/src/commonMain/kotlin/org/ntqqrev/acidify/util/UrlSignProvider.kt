package org.ntqqrev.acidify.util

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.ntqqrev.acidify.common.AppInfo
import org.ntqqrev.acidify.common.SignProvider
import org.ntqqrev.acidify.internal.util.fromHex
import org.ntqqrev.acidify.internal.util.toHex

/**
 * 通过 HTTP 接口进行签名的 [SignProvider] 实现
 * @param url 签名服务的 URL 地址
 * @param httpProxy 可选的 HTTP 代理地址，例如 `http://127.0.0.1:7890`
 */
class UrlSignProvider(val url: String, val httpProxy: String? = null) : SignProvider {
    private val logger = createLogger(this)
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
        logger.v { "请求数据包 (cmd=$cmd, seq=$seq) 的签名" }
        val value = client.post(url) {
            contentType(ContentType.Application.Json)
            setBody(UrlSignRequest(cmd, seq, src.toHex()))
        }.body<UrlSignResponse>().value
        logger.v { "数据包 (cmd=$cmd, seq=$seq) 签名获取成功" }
        return SignProvider.Result(value.sign.fromHex(), value.token.fromHex(), value.extra.fromHex())
    }

    /**
     * 通过 Lagrange 的签名服务提供的额外的 `/appinfo` 接口获取 [AppInfo]，若未提供则返回 `null`
     */
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