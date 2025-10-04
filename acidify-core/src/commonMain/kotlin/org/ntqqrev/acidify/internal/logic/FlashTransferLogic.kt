package org.ntqqrev.acidify.internal.logic

import kotlinx.coroutines.async
import org.ntqqrev.acidify.crypto.hash.SHA1Stream
import org.ntqqrev.acidify.internal.LagrangeClient
import org.ntqqrev.acidify.internal.packet.media.FlashTransferSha1StateV
import org.ntqqrev.acidify.internal.packet.media.FlashTransferUploadBody
import org.ntqqrev.acidify.internal.packet.media.FlashTransferUploadReq
import org.ntqqrev.acidify.internal.packet.media.FlashTransferUploadResp
import org.ntqqrev.acidify.internal.util.flashTransferPostWithBlock
import org.ntqqrev.acidify.internal.util.sha1
import org.ntqqrev.acidify.pb.invoke

internal class FlashTransferLogic(client: LagrangeClient) : AbstractLogic(client) {
    private val logger = client.createLogger(this)
    private val url = "https://multimedia.qfile.qq.com/sliceupload"

    companion object {
        const val CHUNK_SIZE = 1024 * 1024 // 1MB
        private const val TAG = "FlashTransferLogic"
    }

    suspend fun uploadFile(uKey: String, appId: Int, bodyStream: ByteArray): Boolean {
        val chunkCount = (bodyStream.size + CHUNK_SIZE - 1) / CHUNK_SIZE

        val sha1StateList = mutableListOf<ByteArray>()
        val sha1Stream = SHA1Stream()
        for (i in 0 until chunkCount) {
            if (i != chunkCount - 1) {
                val accLength = (i + 1) * CHUNK_SIZE
                val accBuffer = bodyStream.copyOfRange(0, accLength)
                val digest = ByteArray(20)
                sha1Stream.update(accBuffer)
                sha1Stream.hash(digest, false)
                sha1Stream.reset()
                sha1StateList.add(digest)
            } else {
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

    private suspend fun uploadChunk(
        uKey: String,
        appId: Int,
        start: Int,
        sha1StateList: List<ByteArray>,
        body: ByteArray
    ): Boolean = client.async {
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
            val responseBytes = flashTransferPostWithBlock(url, payload)
            val resp = FlashTransferUploadResp(responseBytes)
            val status = resp.get { this.status }

            if (status != "success") {
                logger.e { "FlashTransfer 上传块 $start 失败: $status" }
                return@async false
            }

            logger.d { "FlashTransfer 上传块 $start 成功" }
            true
        } catch (e: Exception) {
            logger.e(e) { "FlashTransfer 上传块 $start 异常: ${e.message}" }
            false
        }
    }.await()
}

