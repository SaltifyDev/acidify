package org.ntqqrev.acidify.message.internal

import org.ntqqrev.acidify.internal.packet.highway.MsgInfo
import org.ntqqrev.acidify.internal.util.readUInt32BE
import org.ntqqrev.acidify.message.BotIncomingSegment
import org.ntqqrev.acidify.message.ImageSubType
import org.ntqqrev.acidify.message.MessageScene
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