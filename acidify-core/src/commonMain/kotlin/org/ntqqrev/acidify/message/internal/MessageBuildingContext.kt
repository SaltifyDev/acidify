package org.ntqqrev.acidify.message.internal

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import org.ntqqrev.acidify.Bot
import org.ntqqrev.acidify.internal.packet.message.Elem
import org.ntqqrev.acidify.internal.packet.message.elem.CommonElem
import org.ntqqrev.acidify.internal.packet.message.elem.Face
import org.ntqqrev.acidify.internal.packet.message.elem.Text
import org.ntqqrev.acidify.internal.packet.message.extra.QBigFaceExtra
import org.ntqqrev.acidify.internal.packet.message.extra.QSmallFaceExtra
import org.ntqqrev.acidify.internal.packet.message.extra.TextResvAttr
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

    override fun mention(uin: Long?) = addAsync {
        Elem {
            it[text] = Text {
                it[textMsg] = "@"
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