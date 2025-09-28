package org.ntqqrev.acidify.message.internal

import org.ntqqrev.acidify.message.BotIncomingSegment

internal interface IncomingSegmentFactory<T : BotIncomingSegment> {
    fun tryParse(ctx: MessageParsingContext): T?

    object Text : IncomingSegmentFactory<BotIncomingSegment.Text> {
        override fun tryParse(ctx: MessageParsingContext): BotIncomingSegment.Text? {
            val text = ctx.tryPeekType { text }
                ?.takeIf { it.get { attr6Buf }.isEmpty() }
                ?: return null
            ctx.consume()
            return BotIncomingSegment.Text(text.get { textMsg })
        }
    }
}