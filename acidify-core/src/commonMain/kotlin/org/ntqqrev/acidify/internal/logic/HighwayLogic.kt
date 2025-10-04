package org.ntqqrev.acidify.internal.logic

import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.withTimeout
import org.ntqqrev.acidify.internal.LagrangeClient
import org.ntqqrev.acidify.internal.packet.media.*
import org.ntqqrev.acidify.internal.util.highwayPostWithBlock
import org.ntqqrev.acidify.internal.util.md5
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
                    extendInfo = extendInfo
                )
                session.upload()
            }
        } catch (_: TimeoutCancellationException) {
            throw Exception("上传超时 (${timeout / 1000}s)")
        }
    }

    /**
     * 上传群头像
     * @param groupUin 群号
     * @param imageData 图片数据
     */
    suspend fun uploadGroupAvatar(groupUin: Long, imageData: ByteArray) {
        val md5 = imageData.md5()
        val extra = GroupAvatarExtra {
            it[type] = 101
            it[this.groupUin] = groupUin
            it[field3] = GroupAvatarExtraField3 {
                it[field1] = 1
            }
            it[field5] = 3
            it[field6] = 1
        }.toByteArray()
        upload(3000, imageData, md5, extra)
    }

    /**
     * 上传群文件
     * @param senderUin 发送者 UIN
     * @param groupUin 群号
     * @param fileName 文件名
     * @param fileData 文件数据
     * @param fileId 文件 ID
     * @param fileKey 文件密钥
     * @param checkKey 校验密钥
     * @param uploadIp 上传服务器 IP
     * @param uploadPort 上传服务器端口
     */
    suspend fun uploadGroupFile(
        senderUin: Long,
        groupUin: Long,
        fileName: String,
        fileData: ByteArray,
        fileId: String,
        fileKey: ByteArray,
        checkKey: ByteArray,
        uploadIp: String,
        uploadPort: Int
    ) {
        // 计算前 10MB 的 MD5
        val md510M = fileData.copyOfRange(0, minOf(10 * 1024 * 1024, fileData.size)).md5()

        // 构建上传扩展信息
        val ext = FileUploadExt {
            it[unknown1] = 100
            it[unknown2] = 1
            it[entry] = FileUploadEntry {
                it[busiBuff] = ExcitingBusiInfo {
                    it[this.senderUin] = senderUin
                    it[receiverUin] = groupUin
                    it[groupCode] = groupUin
                }
                it[fileEntry] = ExcitingFileEntry {
                    it[fileSize] = fileData.size.toLong()
                    it[md5] = fileData.md5()
                    it[this.checkKey] = fileKey
                    it[this.md510M] = md510M
                    it[this.fileId] = fileId
                    it[uploadKey] = checkKey
                }
                it[clientInfo] = ExcitingClientInfo {
                    it[clientType] = 3
                    it[appId] = "100"
                    it[terminalType] = 3
                    it[clientVer] = "1.1.1"
                    it[unknown] = 4
                }
                it[fileNameInfo] = ExcitingFileNameInfo {
                    it[this.fileName] = fileName
                }
                it[host] = ExcitingHostConfig {
                    it[hosts] = listOf(
                        ExcitingHostInfo {
                            it[url] = ExcitingUrlInfo {
                                it[unknown] = 1
                                it[host] = uploadIp
                            }
                            it[port] = uploadPort
                        }
                    )
                }
            }
        }.toByteArray()

        val md5 = fileData.md5()
        upload(71, fileData, md5, ext)
    }

    private class HttpSession(
        private val client: LagrangeClient,
        private val highwayHost: String,
        private val highwayPort: Int,
        private val sigSession: ByteArray,
        private val cmd: Int,
        private val data: ByteArray,
        private val md5: ByteArray,
        private val extendInfo: ByteArray
    ) {
        suspend fun upload() {
            var offset = 0
            while (offset < data.size) {
                val blockSize = minOf(MAX_BLOCK_SIZE, data.size - offset)
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

            val response = client.async {
                highwayPostWithBlock(serverUrl, frame)
            }.await()
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
    }
}