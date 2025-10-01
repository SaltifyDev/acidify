package org.ntqqrev.acidify.message.internal

import org.ntqqrev.acidify.Bot
import org.ntqqrev.acidify.message.BotOutgoingMessageBuilder
import org.ntqqrev.acidify.message.ImageFormat
import org.ntqqrev.acidify.message.ImageSubType
import org.ntqqrev.acidify.message.MessageScene

internal class MessageBuildingContext(
    val bot: Bot,
    val scene: MessageScene,
    val peerUin: Long,
) : BotOutgoingMessageBuilder {
    override fun text(text: String) {
        TODO("Not yet implemented")
    }

    override fun mention(uin: Long?) {
        TODO("Not yet implemented")
    }

    override fun face(faceId: Int, isLarge: Boolean) {
        TODO("Not yet implemented")
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
    ) {
        TODO("Not yet implemented")
    }

    override fun record(rawSilk: ByteArray, duration: Long) {
        TODO("Not yet implemented")
    }

    override fun video(
        raw: ByteArray,
        width: Int,
        height: Int,
        duration: Long,
        thumb: ByteArray,
        thumbFormat: ImageFormat
    ) {
        TODO("Not yet implemented")
    }

    override fun forward(block: suspend BotOutgoingMessageBuilder.Forward.() -> Unit) {
        TODO("Not yet implemented")
    }

    internal class Forward(
        val ctx: MessageBuildingContext
    ) : BotOutgoingMessageBuilder.Forward {
        override fun node(
            senderUin: String,
            senderName: String,
            block: suspend BotOutgoingMessageBuilder.() -> Unit
        ) {
            TODO("Not yet implemented")
        }
    }
}