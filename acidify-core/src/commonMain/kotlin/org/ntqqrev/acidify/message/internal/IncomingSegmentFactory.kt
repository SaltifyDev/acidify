package org.ntqqrev.acidify.message.internal

import org.ntqqrev.acidify.internal.util.readUInt32BE
import org.ntqqrev.acidify.message.BotIncomingSegment
import org.ntqqrev.acidify.message.MessageScene
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
}