package org.ntqqrev.acidify.message.internal

import org.ntqqrev.acidify.internal.packet.highway.MsgInfo
import org.ntqqrev.acidify.internal.packet.message.extra.QBigFaceExtra
import org.ntqqrev.acidify.internal.packet.message.extra.QSmallFaceExtra
import org.ntqqrev.acidify.internal.util.readUInt32BE
import org.ntqqrev.acidify.message.BotIncomingSegment
import org.ntqqrev.acidify.message.ImageSubType
import org.ntqqrev.acidify.message.MessageScene
import org.ntqqrev.acidify.pb.PbObject
import org.ntqqrev.acidify.pb.invoke
import kotlin.math.min

internal interface IncomingSegmentFactory<T : BotIncomingSegment> {
    fun tryParse(ctx: MessageParsingContext): T?

    object Text : IncomingSegmentFactory<BotIncomingSegment.Text> {
        override fun tryParse(ctx: MessageParsingContext): BotIncomingSegment.Text? {
            val text = ctx.tryPeekType { text }
                ?.takeIf { it.get { attr6Buf }.isEmpty() }
                ?: return null
            ctx.consume()
            return BotIncomingSegment.Text(
                text = text.get { textMsg }
            )
        }
    }

    object Mention : IncomingSegmentFactory<BotIncomingSegment.Mention> {
        override fun tryParse(ctx: MessageParsingContext): BotIncomingSegment.Mention? {
            val at = ctx.tryPeekType { text }
                ?.takeIf { it.get { attr6Buf }.size >= 11 }
                ?: return null
            ctx.consume()
            val attr6 = at.get { attr6Buf }
            return BotIncomingSegment.Mention(
                uin = attr6.readUInt32BE(7).takeIf { it > 0 },
                name = at.get { textMsg }
            )
        }
    }

    object Face : IncomingSegmentFactory<BotIncomingSegment.Face> {
        override fun tryParse(ctx: MessageParsingContext): BotIncomingSegment.Face? {
            ctx.tryPeekType { face }?.let { face ->
                ctx.consume()
                val detail = ctx.bot.faceDetailMap[face.get { index }.toString()]
                return BotIncomingSegment.Face(
                    faceId = face.get { index },
                    summary = "[${detail?.qDes?.removePrefix("/") ?: "表情"}]",
                    isLarge = false,
                )
            }

            ctx.tryPeekType { commonElem }?.let { common ->
                val serviceType = common.get { serviceType }
                if (serviceType == 33) {
                    ctx.consume()
                    val extra = PbObject(QSmallFaceExtra, common.get { pbElem })
                    val faceId = extra.get { faceId }
                    val detail = ctx.bot.faceDetailMap[faceId.toString()]
                    return BotIncomingSegment.Face(
                        faceId = faceId,
                        summary = "[${
                            (detail?.qDes ?: extra.get { text }.takeIf { it.isNotEmpty() })
                                ?.removePrefix("/")
                                ?: "表情"
                        }]",
                        isLarge = false,
                    )
                }

                if (serviceType == 37) {
                    ctx.consume()
                    val extra = PbObject(QBigFaceExtra, common.get { pbElem })
                    val faceId = extra.get { faceId }
                    val detail = ctx.bot.faceDetailMap[faceId.toString()]
                    if (ctx.hasNext()) {
                        val text = ctx.tryPeekType { text }
                        if (text?.get { textMsg } == detail?.qDes)
                            ctx.skip()
                    }
                    return BotIncomingSegment.Face(
                        faceId = faceId,
                        summary = "[${
                            (detail?.qDes ?: extra.get { preview }.takeIf { it.isNotEmpty() })
                                ?.removePrefix("/")
                                ?: "超级表情"
                        }]",
                        isLarge = true,
                    )
                }
            }
            return null
        }
    }

    object Reply : IncomingSegmentFactory<BotIncomingSegment.Reply> {
        override fun tryParse(ctx: MessageParsingContext): BotIncomingSegment.Reply? {
            val reply = ctx.tryPeekType { srcMsg }
                ?: return null
            ctx.consume()
            if (ctx.remainingCount >= 2) {
                ctx.skip(
                    if (ctx.tryPeekType { text }?.get { attr6Buf }?.isNotEmpty() == true) {
                        2 // mention + text
                    } else if (ctx.tryPeekType { generalFlags } != null) {
                        if (ctx.message.scene == MessageScene.FRIEND) {
                            2 // generalFlags + elemFlags2
                        } else {
                            min(4, ctx.remainingCount) // generalFlags + elemFlags2 + mention + text
                        }
                    } else 0
                )
            }
            return BotIncomingSegment.Reply(
                sequence = when (ctx.message.scene) {
                    MessageScene.GROUP -> reply.get { origSeqs }.firstOrNull() ?: 0L
                    else -> reply.get { pbReserve }.get { friendSequence }
                }
            )
        }
    }

    object Image : IncomingSegmentFactory<BotIncomingSegment.Image> {
        override fun tryParse(ctx: MessageParsingContext): BotIncomingSegment.Image? {
            val common = ctx.tryPeekType { commonElem } ?: return null
            val businessType = common.get { businessType }
            if (businessType != 10 && businessType != 20) return null
            ctx.consume()
            val msgInfo = MsgInfo(common.get { pbElem })
            val info = msgInfo.get { msgInfoBody }.firstOrNull()?.get { index }?.get { info } ?: return null
            val picBiz = msgInfo.get { extBizInfo }.get { pic }
            return BotIncomingSegment.Image(
                fileId = msgInfo.get { msgInfoBody }.first().get { index }.get { fileUuid },
                width = info.get { width },
                height = info.get { height },
                subType = when (picBiz.get { bizType }) {
                    2 -> ImageSubType.STICKER
                    else -> ImageSubType.NORMAL
                },
                summary = picBiz.get { textSummary }.ifEmpty { "[图片]" },
            )
        }
    }

    object Record : IncomingSegmentFactory<BotIncomingSegment.Record> {
        override fun tryParse(ctx: MessageParsingContext): BotIncomingSegment.Record? {
            val common = ctx.tryPeekType { commonElem } ?: return null
            val businessType = common.get { businessType }
            if (businessType != 12 && businessType != 22) return null
            ctx.consume()
            val msgInfo = MsgInfo(common.get { pbElem })
            val index = msgInfo.get { msgInfoBody }.firstOrNull()?.get { index } ?: return null
            val info = index.get { info }
            return BotIncomingSegment.Record(
                fileId = index.get { fileUuid },
                duration = info.get { time },
            )
        }
    }

    object Video : IncomingSegmentFactory<BotIncomingSegment.Video> {
        override fun tryParse(ctx: MessageParsingContext): BotIncomingSegment.Video? {
            val common = ctx.tryPeekType { commonElem } ?: return null
            val businessType = common.get { businessType }
            if (businessType != 11 && businessType != 21) return null
            ctx.consume()
            val msgInfo = MsgInfo(common.get { pbElem })
            val videoIndex = msgInfo.get { msgInfoBody }.firstOrNull()?.get { index } ?: return null
            val videoInfo = videoIndex.get { info }
            return BotIncomingSegment.Video(
                fileId = videoIndex.get { fileUuid },
                duration = videoInfo.get { time },
                width = videoInfo.get { width },
                height = videoInfo.get { height },
            )
        }
    }
}