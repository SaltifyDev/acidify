package org.ntqqrev.acidify.message.internal

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import org.ntqqrev.acidify.Bot
import org.ntqqrev.acidify.internal.packet.message.Elem
import org.ntqqrev.acidify.internal.packet.message.elem.Text
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

    suspend fun build(): List<PbObject<Elem>> = elemsList.awaitAll().flatten()

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