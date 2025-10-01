package org.ntqqrev.acidify.internal.logic

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import org.ntqqrev.acidify.internal.LagrangeClient
import org.ntqqrev.acidify.internal.packet.media.*
import org.ntqqrev.acidify.internal.util.md5
import org.ntqqrev.acidify.internal.util.toIpString
import org.ntqqrev.acidify.message.MessageScene
import org.ntqqrev.acidify.pb.PbObject
import org.ntqqrev.acidify.pb.invoke
import org.ntqqrev.acidify.util.createHttpClient

internal class HighwayLogic(client: LagrangeClient) : AbstractLogic(client) {
    private var highwayHost: String = ""
    private var highwayPort: Int = 0
    private var sigSession: ByteArray = ByteArray(0)
    private val httpClient = createHttpClient { }

    companion object {
        const val MAX_BLOCK_SIZE = 1024 * 1024 // 1MB
    }

    fun setHighwayUrl(host: String, port: Int, sigSession: ByteArray) {
        this.highwayHost = host
        this.highwayPort = port
        this.sigSession = sigSession
    }

    suspend fun uploadImage(
        image: ByteArray,
        imageMd5: ByteArray,
        imageSha1: ByteArray,
        uploadResp: PbObject<UploadResp>,
        messageScene: MessageScene,
    ) {
        val cmd = if (messageScene == MessageScene.FRIEND) 1003 else 1004
        upload(
            cmd = cmd,
            data = image,
            md5 = imageMd5,
            extendInfo = buildExtendInfo(uploadResp, imageSha1)
        )
    }

    suspend fun uploadRecord(
        record: ByteArray,
        recordMd5: ByteArray,
        recordSha1: ByteArray,
        uploadResp: PbObject<UploadResp>,
    ) {
        upload(
            cmd = 1008,
            data = record,
            md5 = recordMd5,
            extendInfo = buildExtendInfo(uploadResp, recordSha1)
        )
    }

    suspend fun uploadVideo(
        video: ByteArray,
        videoMd5: ByteArray,
        videoSha1: ByteArray,
        thumbnail: ByteArray,
        thumbnailMd5: ByteArray,
        thumbnailSha1: ByteArray,
        uploadResp: PbObject<UploadResp>,
        messageScene: MessageScene,
    ) {
        val videoCmd = if (messageScene == MessageScene.FRIEND) 1001 else 1005
        upload(
            cmd = videoCmd,
            data = video,
            md5 = videoMd5,
            extendInfo = buildExtendInfo(uploadResp, videoSha1, bodyIndex = 0)
        )

        val thumbnailCmd = if (messageScene == MessageScene.FRIEND) 1002 else 1006
        upload(
            cmd = thumbnailCmd,
            data = thumbnail,
            md5 = thumbnailMd5,
            extendInfo = buildExtendInfo(uploadResp, thumbnailSha1, bodyIndex = 1)
        )
    }

    suspend fun uploadGroupFile(
        file: ByteArray,
        fileName: String,
        fileMd5: ByteArray,
        uploadResp: PbObject<UploadResp>,
    ): Unit = TODO("build FileUploadExt")

    private fun buildExtendInfo(uploadResp: PbObject<UploadResp>, sha1: ByteArray, bodyIndex: Int = 0): ByteArray {
        val msgInfoBodyList = uploadResp.get { msgInfo }.get { msgInfoBody }
        val index = msgInfoBodyList.getOrNull(bodyIndex)?.get { index }
        val fileUuidValue = index?.get { fileUuid } ?: ""

        return NTV2RichMediaHighwayExt {
            it[fileUuid] = fileUuidValue
            it[uKey] = uploadResp.get { uKey }
            it[network] = NTHighwayNetwork {
                it[iPv4s] = uploadResp.get { iPv4s }.map { ipv4 ->
                    NTHighwayIPv4 {
                        it[domain] = NTHighwayDomain {
                            it[isEnable] = true
                            it[iP] = ipv4.get { outIP }.toIpString()
                        }
                        it[port] = ipv4.get { outPort }
                    }
                }
            }
            it[msgInfoBody] = msgInfoBodyList
            it[blockSize] = MAX_BLOCK_SIZE
            it[hash] = NTHighwayHash {
                it[fileSha1] = listOf(sha1)
            }
        }.toByteArray()
    }

    private suspend fun upload(
        cmd: Int,
        data: ByteArray,
        md5: ByteArray,
        extendInfo: ByteArray,
        timeout: Long = 1200_000L // 1200 seconds
    ) {
        try {
            withTimeout(timeout) {
                val session = HttpSession(
                    client = client,
                    highwayHost = highwayHost,
                    highwayPort = highwayPort,
                    sigSession = sigSession,
                    cmd = cmd,
                    data = data,
                    md5 = md5,
                    extendInfo = extendInfo,
                    httpClient = httpClient,
                    timeout = timeout,
                )
                session.upload()
            }
        } catch (_: TimeoutCancellationException) {
            throw Exception("上传超时 (${timeout / 1000}s)")
        }
    }

    private class HttpSession(
        private val client: LagrangeClient,
        private val highwayHost: String,
        private val highwayPort: Int,
        private val sigSession: ByteArray,
        private val cmd: Int,
        private val data: ByteArray,
        private val md5: ByteArray,
        private val extendInfo: ByteArray,
        private val httpClient: HttpClient,
        private val timeout: Long,
    ) {
        suspend fun upload() {
            var offset = 0
            while (offset < data.size) {
                val blockSize = minOf(HighwayLogic.MAX_BLOCK_SIZE, data.size - offset)
                val block = data.copyOfRange(offset, offset + blockSize)

                uploadBlock(block, offset)
                offset += blockSize
            }
        }

        private suspend fun uploadBlock(block: ByteArray, offset: Int) {
            val blockMd5 = block.md5()
            val head = buildPicUpHead(offset, block.size, blockMd5)
            val frame = packFrame(head, block)

            val serverUrl =
                "http://$highwayHost:$highwayPort/cgi-bin/httpconn?htcmd=0x6FF0087&uin=${client.sessionStore.uin}"

            val response = uploadFrame(frame, serverUrl)
            val (responseHead, _) = unpackFrame(response)

            val headData = RespDataHighwayHead(responseHead)
            val errorCode = headData.get { errorCode }

            if (errorCode != 0) {
                throw Exception("[Highway] HTTP Upload failed with code $errorCode")
            }
        }

        private fun buildPicUpHead(offset: Int, bodyLength: Int, bodyMd5: ByteArray): ByteArray {
            return ReqDataHighwayHead {
                it[msgBaseHead] = DataHighwayHead {
                    it[version] = 1
                    it[uin] = client.sessionStore.uin.toString()
                    it[command] = "PicUp.DataUp"
                    it[seq] = 0
                    it[retryTimes] = 0
                    it[appId] = 1600001604
                    it[dataFlag] = 16
                    it[commandId] = cmd
                }
                it[msgSegHead] = SegHead {
                    it[serviceId] = 0
                    it[filesize] = data.size.toLong()
                    it[dataOffset] = offset.toLong()
                    it[dataLength] = bodyLength
                    it[serviceTicket] = sigSession
                    it[md5] = bodyMd5
                    it[fileMd5] = this@HttpSession.md5
                    it[cacheAddr] = 0
                    it[cachePort] = 0
                }
                it[bytesReqExtendInfo] = extendInfo
                it[timestamp] = 0L
                it[msgLoginSigHead] = LoginSigHead {
                    it[uint32LoginSigType] = 8
                    it[appId] = 1600001604
                }
            }.toByteArray()
        }

        private fun packFrame(head: ByteArray, body: ByteArray): ByteArray {
            val totalLength = 9 + head.size + body.size + 1
            val buffer = ByteArray(totalLength)

            buffer[0] = 0x28
            buffer[1] = (head.size ushr 24).toByte()
            buffer[2] = (head.size ushr 16).toByte()
            buffer[3] = (head.size ushr 8).toByte()
            buffer[4] = head.size.toByte()

            buffer[5] = (body.size ushr 24).toByte()
            buffer[6] = (body.size ushr 16).toByte()
            buffer[7] = (body.size ushr 8).toByte()
            buffer[8] = body.size.toByte()

            head.copyInto(buffer, 9)
            body.copyInto(buffer, 9 + head.size)

            buffer[totalLength - 1] = 0x29

            return buffer
        }

        private fun unpackFrame(frame: ByteArray): Pair<ByteArray, ByteArray> {
            require(frame[0] == 0x28.toByte() && frame[frame.size - 1] == 0x29.toByte()) {
                "Invalid frame!"
            }

            val headLen =
                ((frame[1].toInt() and 0xFF) shl 24) or ((frame[2].toInt() and 0xFF) shl 16) or ((frame[3].toInt() and 0xFF) shl 8) or (frame[4].toInt() and 0xFF)

            val bodyLen =
                ((frame[5].toInt() and 0xFF) shl 24) or ((frame[6].toInt() and 0xFF) shl 16) or ((frame[7].toInt() and 0xFF) shl 8) or (frame[8].toInt() and 0xFF)

            val head = frame.copyOfRange(9, 9 + headLen)
            val body = frame.copyOfRange(9 + headLen, 9 + headLen + bodyLen)

            return Pair(head, body)
        }

        private suspend fun uploadFrame(frame: ByteArray, serverUrl: String): ByteArray {
            val response = httpClient.post(serverUrl) {
                headers {
                    append(HttpHeaders.Connection, "keep-alive")
                    append(HttpHeaders.AcceptEncoding, "identity")
                    append(HttpHeaders.UserAgent, "Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.2)")
                    append(HttpHeaders.ContentLength, frame.size.toString())
                }
                setBody(frame)
                this@post.timeout {
                    requestTimeoutMillis = timeout
                    connectTimeoutMillis = timeout / 2
                    socketTimeoutMillis = timeout
                }
            }
            return response.readRawBytes()
        }
    }
}