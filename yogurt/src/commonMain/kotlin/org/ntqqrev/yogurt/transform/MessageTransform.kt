package org.ntqqrev.yogurt.transform

import io.ktor.client.*
import io.ktor.server.application.*
import io.ktor.server.plugins.di.*
import org.ntqqrev.acidify.Bot
import org.ntqqrev.acidify.message.*
import org.ntqqrev.acidify.util.log.Logger
import org.ntqqrev.milky.IncomingMessage
import org.ntqqrev.milky.IncomingSegment
import org.ntqqrev.milky.OutgoingSegment
import org.ntqqrev.yogurt.codec.*
import org.ntqqrev.yogurt.util.FriendCache
import org.ntqqrev.yogurt.util.GroupCache
import org.ntqqrev.yogurt.util.resolveGroupMemberCache
import org.ntqqrev.yogurt.util.resolveUri

suspend fun Application.transformMessage(msg: BotIncomingMessage): IncomingMessage? {
    return when (msg.scene) {
        MessageScene.FRIEND -> {
            val friendCache = dependencies.resolve<FriendCache>()
            val friend = friendCache[msg.peerUin] ?: return null
            IncomingMessage.Friend(
                peerId = msg.peerUin,
                messageSeq = msg.sequence,
                senderId = msg.senderUin,
                time = msg.timestamp,
                segments = msg.segments.map { transformSegment(it) },
                friend = friend.toMilkyEntity()
            )
        }

        MessageScene.GROUP -> {
            val groupCache = dependencies.resolve<GroupCache>()
            val group = groupCache[msg.peerUin] ?: return null
            val memberCache = resolveGroupMemberCache(msg.peerUin) ?: return null
            val member = memberCache[msg.senderUin] ?: return null
            IncomingMessage.Group(
                peerId = msg.peerUin,
                messageSeq = msg.sequence,
                senderId = msg.senderUin,
                time = msg.timestamp,
                segments = msg.segments.map { transformSegment(it) },
                group = group.toMilkyEntity(),
                groupMember = member.toMilkyEntity(group.uin),
            )
        }

        else -> null
    }
}

suspend fun Application.transformSegment(segment: BotIncomingSegment): IncomingSegment {
    val bot = dependencies.resolve<Bot>()
    return when (segment) {
        is BotIncomingSegment.Text -> IncomingSegment.Text(
            data = IncomingSegment.Text.Data(
                text = segment.text
            )
        )

        is BotIncomingSegment.Mention -> if (segment.uin != null) {
            IncomingSegment.Mention(
                data = IncomingSegment.Mention.Data(
                    userId = segment.uin!!
                )
            )
        } else {
            IncomingSegment.MentionAll(
                data = IncomingSegment.MentionAll.Data()
            )
        }

        is BotIncomingSegment.Face -> IncomingSegment.Face(
            data = IncomingSegment.Face.Data(
                faceId = segment.faceId.toString()
            )
        )

        is BotIncomingSegment.Reply -> IncomingSegment.Reply(
            data = IncomingSegment.Reply.Data(
                messageSeq = segment.sequence
            )
        )

        is BotIncomingSegment.Image -> IncomingSegment.Image(
            data = IncomingSegment.Image.Data(
                resourceId = segment.fileId,
                tempUrl = bot.getDownloadUrl(segment.fileId),
                width = segment.width,
                height = segment.height,
                summary = segment.summary,
                subType = segment.subType.toMilkyString()
            )
        )

        is BotIncomingSegment.Record -> IncomingSegment.Record(
            data = IncomingSegment.Record.Data(
                resourceId = segment.fileId,
                tempUrl = bot.getDownloadUrl(segment.fileId),
                duration = segment.duration
            )
        )

        is BotIncomingSegment.Video -> IncomingSegment.Video(
            data = IncomingSegment.Video.Data(
                resourceId = segment.fileId,
                tempUrl = bot.getDownloadUrl(segment.fileId),
                duration = segment.duration,
                width = segment.width,
                height = segment.height
            )
        )

        is BotIncomingSegment.File -> IncomingSegment.File(
            data = IncomingSegment.File.Data(
                fileId = segment.fileId,
                fileName = segment.fileName,
                fileSize = segment.fileSize,
                fileHash = segment.fileHash
            )
        )

        is BotIncomingSegment.Forward -> IncomingSegment.Forward(
            data = IncomingSegment.Forward.Data(
                forwardId = segment.resId,
            )
        )

        is BotIncomingSegment.MarketFace -> IncomingSegment.MarketFace(
            data = IncomingSegment.MarketFace.Data(
                url = segment.url,
            )
        )

        is BotIncomingSegment.LightApp -> IncomingSegment.LightApp(
            data = IncomingSegment.LightApp.Data(
                appName = segment.appName,
                jsonPayload = segment.jsonPayload
            )
        )
    }
}

class YogurtMessageBuildingContext(
    val application: Application,
    val builder: BotOutgoingMessageBuilder,
    val scene: MessageScene,
    val peerUin: Long,
    val httpClient: HttpClient
) : BotOutgoingMessageBuilder by builder {
    fun switchTo(newBuilder: BotOutgoingMessageBuilder): YogurtMessageBuildingContext {
        return YogurtMessageBuildingContext(application, newBuilder, scene, peerUin, httpClient)
    }
}

suspend fun YogurtMessageBuildingContext.applySegment(segment: OutgoingSegment) {
    val logger = application.dependencies.resolve<Logger>()
    when (segment) {
        is OutgoingSegment.Text -> {
            text(segment.data.text)
        }

        is OutgoingSegment.Mention -> {
            if (scene == MessageScene.FRIEND) {
                // 私聊不支持 at，转换为文本
                text("@${segment.data.userId} ")
                return
            }
            val groupMemberCache = application.resolveGroupMemberCache(peerUin)
            mention(
                segment.data.userId,
                groupMemberCache?.get(segment.data.userId)?.nickname ?: segment.data.userId.toString()
            )
        }

        is OutgoingSegment.MentionAll -> {
            mention(null, "全体成员")
        }

        is OutgoingSegment.Face -> {
            face(segment.data.faceId.toInt())
        }

        is OutgoingSegment.Reply -> {
            reply(segment.data.messageSeq)
        }

        is OutgoingSegment.Image -> {
            val imageData = resolveUri(segment.data.uri, httpClient)
            val imageInfo = getImageInfo(imageData)
            image(
                raw = imageData,
                format = imageInfo.format,
                width = imageInfo.width,
                height = imageInfo.height,
                subType = segment.data.subType.toImageSubType(),
                summary = segment.data.summary ?: "[图片]"
            )
        }

        is OutgoingSegment.Record -> {
            val audioData = resolveUri(segment.data.uri, httpClient)
            // try to convert to pcm, if fails, assume it's already pcm
            val pcmData = try {
                audioToPcm(audioData)
            } catch (e: Exception) {
                logger.w(e) { "语音 ${segment.data.uri} 转 PCM 失败，尝试直接编码" }
                audioData
            }
            val silkData = silkEncode(pcmData)
            logger.d { "语音 ${segment.data.uri} 编码完成" }
            val duration = calculatePcmDuration(pcmData)
            logger.d { "语音时长 ${duration.inWholeSeconds} 秒" }
            record(
                rawSilk = silkData,
                duration = duration.inWholeSeconds
            )
        }

        is OutgoingSegment.Video -> {
            val videoData = resolveUri(segment.data.uri, httpClient)
            val videoInfo = getVideoInfo(videoData)
            logger.d { "视频宽高 ${videoInfo.width}x${videoInfo.height}，时长 ${videoInfo.duration.inWholeSeconds} 秒" }
            val thumbData = if (segment.data.thumbUri != null) {
                resolveUri(segment.data.thumbUri, httpClient)
            } else {
                getVideoFirstFrameJpg(videoData)
            }
            val thumbInfo = getImageInfo(thumbData)

            video(
                raw = videoData,
                width = videoInfo.width,
                height = videoInfo.height,
                duration = videoInfo.duration.inWholeSeconds,
                thumb = thumbData,
                thumbFormat = thumbInfo.format
            )
        }

        is OutgoingSegment.Forward -> {
            forward {
                segment.data.messages.forEach { forwardedMsg ->
                    node(forwardedMsg.userId, forwardedMsg.senderName) {
                        val nodeContext = switchTo(this@node)
                        forwardedMsg.segments.forEach { seg ->
                            nodeContext.applySegment(seg)
                        }
                    }
                }
            }
        }
    }
}

fun ImageSubType.toMilkyString() = when (this) {
    ImageSubType.NORMAL -> "normal"
    ImageSubType.STICKER -> "sticker"
}

fun String.toImageSubType() = when (this) {
    "normal" -> ImageSubType.NORMAL
    "sticker" -> ImageSubType.STICKER
    else -> ImageSubType.NORMAL
}

fun String.toMessageScene() = when (this) {
    "friend" -> MessageScene.FRIEND
    "group" -> MessageScene.GROUP
    "temp" -> MessageScene.TEMP
    else -> throw IllegalArgumentException("Unknown message scene: $this")
}