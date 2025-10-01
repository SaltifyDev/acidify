package org.ntqqrev.yogurt.transform

import io.ktor.server.application.*
import io.ktor.server.plugins.di.*
import org.ntqqrev.acidify.Bot
import org.ntqqrev.acidify.message.BotIncomingMessage
import org.ntqqrev.acidify.message.BotIncomingSegment
import org.ntqqrev.acidify.message.ImageSubType
import org.ntqqrev.acidify.message.MessageScene
import org.ntqqrev.milky.IncomingMessage
import org.ntqqrev.milky.IncomingSegment
import org.ntqqrev.yogurt.util.FriendCache
import org.ntqqrev.yogurt.util.GroupCache
import org.ntqqrev.yogurt.util.resolveGroupMemberCache

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

fun ImageSubType.toMilkyString() = when (this) {
    ImageSubType.NORMAL -> "normal"
    ImageSubType.STICKER -> "sticker"
}

fun String.toMessageScene() = when (this) {
    "friend" -> MessageScene.FRIEND
    "group" -> MessageScene.GROUP
    "temp" -> MessageScene.TEMP
    else -> throw IllegalArgumentException("Unknown message scene: $this")
}