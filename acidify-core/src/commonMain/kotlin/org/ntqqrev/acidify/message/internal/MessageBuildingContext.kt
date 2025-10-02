package org.ntqqrev.acidify.message.internal

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import org.ntqqrev.acidify.Bot
import org.ntqqrev.acidify.crypto.hash.MD5
import org.ntqqrev.acidify.internal.packet.message.Elem
import org.ntqqrev.acidify.internal.packet.message.elem.CommonElem
import org.ntqqrev.acidify.internal.packet.message.elem.Face
import org.ntqqrev.acidify.internal.packet.message.elem.Text
import org.ntqqrev.acidify.internal.packet.message.extra.QBigFaceExtra
import org.ntqqrev.acidify.internal.packet.message.extra.QSmallFaceExtra
import org.ntqqrev.acidify.internal.packet.message.extra.TextResvAttr
import org.ntqqrev.acidify.internal.service.message.RichMediaUpload
import org.ntqqrev.acidify.internal.util.sha1
import org.ntqqrev.acidify.message.BotOutgoingMessageBuilder
import org.ntqqrev.acidify.message.ImageFormat
import org.ntqqrev.acidify.message.ImageSubType
import org.ntqqrev.acidify.message.MessageScene
import org.ntqqrev.acidify.pb.PbObject
import org.ntqqrev.acidify.pb.invoke

internal class MessageBuildingContext(
    val bot: Bot,
    val scene: MessageScene,
    val peerUin: Long,
    val peerUid: String,
) : BotOutgoingMessageBuilder {
    private val logger = bot.createLogger(this)
    private val elemsList = mutableListOf<Deferred<List<PbObject<Elem>>>>()

    private fun addAsync(elem: suspend () -> PbObject<Elem>) {
        elemsList.add(bot.async { listOf(elem()) })
    }

    private fun addMultipleAsync(elems: suspend () -> List<PbObject<Elem>>) {
        elemsList.add(bot.async { elems() })
    }

    override fun text(text: String) = addAsync {
        Elem {
            it[this.text] = Text {
                it[textMsg] = text
            }
        }
    }

    override fun mention(uin: Long?, name: String) = addAsync {
        Elem {
            it[text] = Text {
                it[textMsg] = "@$name"
                it[pbReserve] = TextResvAttr {
                    it[atType] = if (uin == null) 1 else 2  // 1 for @all, 2 for @specific
                    if (uin != null) {
                        it[atMemberUin] = uin
                        it[atMemberUid] = bot.getUidByUin(uin)
                    }
                }.toByteArray()
            }
        }
    }

    override fun face(faceId: Int, isLarge: Boolean) = addAsync {
        val faceDetail = bot.faceDetailMap[faceId.toString()]
            ?: throw NoSuchElementException("要发送的表情 ID 不存在: $faceId")

        if (isLarge) {
            Elem {
                it[commonElem] = CommonElem {
                    it[serviceType] = 37
                    it[pbElem] = QBigFaceExtra {
                        it[aniStickerPackId] = faceDetail.aniStickerPackId.toString()
                        it[aniStickerId] = faceDetail.aniStickerId.toString()
                        it[this.faceId] = faceId
                        it[field4] = 1
                        it[aniStickerType] = faceDetail.aniStickerType
                        it[field6] = ""
                        it[preview] = faceDetail.qDes
                        it[field9] = 1
                    }.toByteArray()
                    it[businessType] = faceDetail.aniStickerType
                }
            }
        }

        if (faceId >= 260) {
            Elem {
                it[commonElem] = CommonElem {
                    it[serviceType] = 33
                    it[pbElem] = QSmallFaceExtra {
                        it[this.faceId] = faceId
                        it[text] = faceDetail.qDes
                        it[compatText] = faceDetail.qDes
                    }.toByteArray()
                    it[businessType] = faceDetail.aniStickerType
                }
            }
        }

        Elem {
            it[face] = Face {
                it[index] = faceId
            }
        }
    }

    override fun reply(sequence: Long) {
        TODO("Not yet implemented")
    }

    override fun image(
        raw: ByteArray,
        format: ImageFormat,
        width: Int,
        height: Int,
        subType: ImageSubType,
        summary: String
    ) = addAsync {
        val imageMd5 = MD5.hashHex(raw)
        val imageSha1Bytes = raw.sha1()
        val imageSha1 = imageSha1Bytes.toHexString()

        val uploadResp = when (scene) {
            MessageScene.FRIEND -> {
                bot.client.callService(
                    RichMediaUpload.PrivateImage,
                    RichMediaUpload.ImageUploadRequest(
                        imageData = raw,
                        imageMd5 = imageMd5,
                        imageSha1 = imageSha1,
                        imageExt = ".${format.ext}",
                        width = width,
                        height = height,
                        picFormat = format.underlying,
                        subType = subType.underlying,
                        textSummary = summary
                    )
                )
            }

            MessageScene.GROUP -> {
                bot.client.callService(
                    RichMediaUpload.GroupImage,
                    RichMediaUpload.ImageUploadRequest(
                        imageData = raw,
                        imageMd5 = imageMd5,
                        imageSha1 = imageSha1,
                        imageExt = ".${format.ext}",
                        width = width,
                        height = height,
                        picFormat = format.underlying,
                        subType = subType.underlying,
                        textSummary = summary,
                        groupUin = peerUin
                    )
                )
            }

            else -> throw IllegalArgumentException("不支持的消息场景: $scene")
        }

        uploadResp.respObj.get { uKey }.takeIf { it.isNotEmpty() }?.let {
            bot.client.flashTransferLogic.uploadFile(
                uKey = it,
                appId = if (scene == MessageScene.FRIEND) 1406 else 1407,
                bodyStream = raw
            )
        } ?: logger.d { "uKey 为空，服务器可能已存在该图片，跳过上传" }

        val msgInfo = uploadResp.respObj.get { msgInfo }
        val businessType = when (scene) {
            MessageScene.FRIEND -> 10
            MessageScene.GROUP -> 20
            else -> throw IllegalArgumentException("不支持的消息场景: $scene")
        }

        Elem {
            it[commonElem] = CommonElem {
                it[serviceType] = 48
                it[pbElem] = msgInfo.toByteArray()
                it[this.businessType] = businessType
            }
        }
    }

    override fun record(rawSilk: ByteArray, duration: Long) = addAsync {
        val recordMd5 = MD5.hashHex(rawSilk)
        val recordSha1Bytes = rawSilk.sha1()
        val recordSha1 = recordSha1Bytes.toHexString()

        val uploadResp = when (scene) {
            MessageScene.FRIEND -> {
                bot.client.callService(
                    RichMediaUpload.PrivateRecord,
                    RichMediaUpload.RecordUploadRequest(
                        audioData = rawSilk,
                        audioMd5 = recordMd5,
                        audioSha1 = recordSha1,
                        audioDuration = duration.toInt()
                    )
                )
            }

            MessageScene.GROUP -> {
                bot.client.callService(
                    RichMediaUpload.GroupRecord,
                    RichMediaUpload.RecordUploadRequest(
                        audioData = rawSilk,
                        audioMd5 = recordMd5,
                        audioSha1 = recordSha1,
                        audioDuration = duration.toInt(),
                        groupUin = peerUin
                    )
                )
            }

            else -> throw IllegalArgumentException("不支持的消息场景: $scene")
        }

        uploadResp.respObj.get { uKey }.takeIf { it.isNotEmpty() }?.let {
            bot.client.flashTransferLogic.uploadFile(
                uKey = it,
                appId = if (scene == MessageScene.FRIEND) 1402 else 1403,
                bodyStream = rawSilk
            )
        } ?: logger.d { "uKey 为空，服务器可能已存在该语音，跳过上传" }

        val msgInfo = uploadResp.respObj.get { msgInfo }
        val businessType = when (scene) {
            MessageScene.FRIEND -> 12
            MessageScene.GROUP -> 22
            else -> throw IllegalArgumentException("不支持的消息场景: $scene")
        }

        Elem {
            it[commonElem] = CommonElem {
                it[serviceType] = 48
                it[pbElem] = msgInfo.toByteArray()
                it[this.businessType] = businessType
            }
        }
    }

    override fun video(
        raw: ByteArray,
        width: Int,
        height: Int,
        duration: Long,
        thumb: ByteArray,
        thumbFormat: ImageFormat
    ) = addAsync {
        val videoMd5 = MD5.hashHex(raw)
        val videoSha1Bytes = raw.sha1()
        val videoSha1 = videoSha1Bytes.toHexString()

        val thumbMd5 = MD5.hashHex(thumb)
        val thumbSha1Bytes = thumb.sha1()
        val thumbSha1 = thumbSha1Bytes.toHexString()

        val uploadResp = when (scene) {
            MessageScene.FRIEND -> {
                bot.client.callService(
                    RichMediaUpload.PrivateVideo,
                    RichMediaUpload.VideoUploadRequest(
                        videoData = raw,
                        videoMd5 = videoMd5,
                        videoSha1 = videoSha1,
                        videoWidth = width,
                        videoHeight = height,
                        videoDuration = duration.toInt(),
                        thumbnailData = thumb,
                        thumbnailMd5 = thumbMd5,
                        thumbnailSha1 = thumbSha1,
                        thumbnailExt = thumbFormat.ext,
                        thumbnailPicFormat = thumbFormat.underlying
                    )
                )
            }

            MessageScene.GROUP -> {
                bot.client.callService(
                    RichMediaUpload.GroupVideo,
                    RichMediaUpload.VideoUploadRequest(
                        videoData = raw,
                        videoMd5 = videoMd5,
                        videoSha1 = videoSha1,
                        videoWidth = width,
                        videoHeight = height,
                        videoDuration = duration.toInt(),
                        thumbnailData = thumb,
                        thumbnailMd5 = thumbMd5,
                        thumbnailSha1 = thumbSha1,
                        thumbnailExt = thumbFormat.ext,
                        thumbnailPicFormat = thumbFormat.underlying,
                        groupUin = peerUin
                    )
                )
            }

            else -> throw IllegalArgumentException("不支持的消息场景: $scene")
        }

        // 上传视频文件
        uploadResp.respObj.get { uKey }.takeIf { it.isNotEmpty() }?.let {
            bot.client.flashTransferLogic.uploadFile(
                uKey = it,
                appId = if (scene == MessageScene.FRIEND) 1413 else 1415,
                bodyStream = raw
            )
        } ?: logger.d { "uKey 为空，服务器可能已存在该视频，跳过上传" }

        // 上传缩略图
        uploadResp.respObj.get { subFileInfos }.getOrNull(0)?.get { uKey }
            ?.takeIf { it.isNotEmpty() }?.let {
                bot.client.flashTransferLogic.uploadFile(
                    uKey = it,
                    appId = if (scene == MessageScene.FRIEND) 1414 else 1416,
                    bodyStream = thumb
                )
            } ?: logger.d { "视频缩略图 uKey 为空，服务器可能已存在该缩略图，跳过上传" }

        val msgInfo = uploadResp.respObj.get { msgInfo }
        val businessType = when (scene) {
            MessageScene.FRIEND -> 11
            MessageScene.GROUP -> 21
            else -> throw IllegalArgumentException("不支持的消息场景: $scene")
        }

        Elem {
            it[commonElem] = CommonElem {
                it[serviceType] = 48
                it[pbElem] = msgInfo.toByteArray()
                it[this.businessType] = businessType
            }
        }
    }

    override fun forward(block: suspend BotOutgoingMessageBuilder.Forward.() -> Unit) {
        TODO("Not yet implemented")
    }

    suspend fun build(): List<PbObject<Elem>> = elemsList.awaitAll().flatten()

    internal class Forward(
        val ctx: MessageBuildingContext
    ) : BotOutgoingMessageBuilder.Forward {
        override fun node(
            senderUin: Long,
            senderName: String,
            block: suspend BotOutgoingMessageBuilder.() -> Unit
        ) {
            TODO("Not yet implemented")
        }
    }
}