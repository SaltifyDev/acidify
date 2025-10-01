package org.ntqqrev.acidify.internal.logic

import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import org.ntqqrev.acidify.internal.LagrangeClient
import org.ntqqrev.acidify.internal.packet.media.FlashTransferSha1StateV
import org.ntqqrev.acidify.internal.packet.media.FlashTransferUploadBody
import org.ntqqrev.acidify.internal.packet.media.FlashTransferUploadReq
import org.ntqqrev.acidify.internal.packet.media.FlashTransferUploadResp
import org.ntqqrev.acidify.internal.util.sha1
import org.ntqqrev.acidify.pb.invoke

/**
 * FlashTransfer 上传逻辑
 * 用于通过闪传方式上传文件
 */
internal class FlashTransferLogic(client: LagrangeClient) : AbstractLogic(client) {
    private val httpClient = HttpClient()
    private val logger = client.createLogger(this)
    private val url = "https://multimedia.qfile.qq.com/sliceupload"

    companion object {
        const val CHUNK_SIZE = 1024 * 1024 // 1MB
        private const val TAG = "FlashTransferLogic"
    }

    /**
     * 上传文件
     * @param uKey 上传密钥
     * @param appId 应用 ID
     * @param bodyStream 文件数据
     * @return 上传是否成功
     */
    suspend fun uploadFile(uKey: String, appId: Int, bodyStream: ByteArray): Boolean {
        val chunkCount = (bodyStream.size + CHUNK_SIZE - 1) / CHUNK_SIZE

        // 预计算所有块的 SHA1 状态
        val sha1StateList = mutableListOf<ByteArray>()
        for (i in 0 until chunkCount) {
            if (i != chunkCount - 1) {
                // 不是最后一块，计算累积 SHA1
                val accLength = (i + 1) * CHUNK_SIZE
                val accBuffer = bodyStream.copyOfRange(0, accLength)
                val digest = accBuffer.sha1()
                sha1StateList.add(digest)
            } else {
                // 最后一块，计算整个文件的 SHA1
                val digest = bodyStream.sha1()
                sha1StateList.add(digest)
            }
        }

        // 逐块上传
        for (i in 0 until chunkCount) {
            val chunkStart = i * CHUNK_SIZE
            val chunkLength = minOf(CHUNK_SIZE, bodyStream.size - chunkStart)

            val uploadBuffer = bodyStream.copyOfRange(chunkStart, chunkStart + chunkLength)

            val success = uploadChunk(
                uKey = uKey,
                appId = appId,
                start = chunkStart,
                sha1StateList = sha1StateList,
                body = uploadBuffer
            )

            if (!success) {
                return false
            }
        }

        return true
    }

    /**
     * 上传单个数据块
     */
    private suspend fun uploadChunk(
        uKey: String,
        appId: Int,
        start: Int,
        sha1StateList: List<ByteArray>,
        body: ByteArray
    ): Boolean {
        val chunkSha1 = body.sha1()
        val end = start + body.size - 1

        // 构建请求
        val req = FlashTransferUploadReq {
            it[field1] = 0
            it[this.appId] = appId
            it[field3] = 2
            it[this.body] = FlashTransferUploadBody {
                it[field1] = ByteArray(0)
                it[this.uKey] = uKey
                it[this.start] = start
                it[this.end] = end
                it[sha1] = chunkSha1
                it[sha1StateV] = FlashTransferSha1StateV {
                    it[state] = sha1StateList
                }
                it[this.body] = body
            }
        }

        val payload = req.toByteArray()

        try {
            // 发送 HTTP POST 请求
            val response = httpClient.post(url) {
                headers {
                    append(HttpHeaders.Accept, "*/*")
                    append(HttpHeaders.Expect, "100-continue")
                    append(HttpHeaders.Connection, "Keep-Alive")
                    append(HttpHeaders.AcceptEncoding, "gzip")
                }
                setBody(payload)
            }

            // 解析响应
            val responseBytes = response.readBytes()
            val resp = FlashTransferUploadResp(responseBytes)
            val status = resp.get { this.status }

            if (status != "success") {
                logger.e { "FlashTransfer 上传块 $start 失败: $status" }
                return false
            }

            logger.d { "FlashTransfer 上传块 $start 成功" }
            return true
        } catch (e: Exception) {
            logger.e(e) { "FlashTransfer 上传块 $start 异常: ${e.message}" }
            return false
        }
    }

    /**
     * 关闭 HTTP 客户端
     */
    fun close() {
        httpClient.close()
    }
}

