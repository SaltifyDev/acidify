package org.ntqqrev.acidify.message.internal

import org.ntqqrev.acidify.Bot
import org.ntqqrev.acidify.internal.packet.message.Elem
import org.ntqqrev.acidify.message.BotIncomingMessage
import org.ntqqrev.acidify.pb.PbObject
import org.ntqqrev.acidify.pb.PbOptional
import org.ntqqrev.acidify.pb.PbSchema

internal class MessageParsingContext(
    val message: BotIncomingMessage,
    val elems: List<PbObject<Elem>>,
    val bot: Bot,
) {
    var currentIndex = 0

    val remainingCount: Int
        get() = elems.size - currentIndex

    fun hasNext(): Boolean = currentIndex < elems.size

    fun peek(): PbObject<Elem> = elems[currentIndex]

    fun <T : PbSchema> tryPeekType(type: PbOptional<PbObject<T>>) : PbObject<T>? = peek()[type]

    inline fun <T : PbSchema> tryPeekType(typeProvider: Elem.() -> PbOptional<PbObject<T>>) =
        tryPeekType(Elem.typeProvider())

    fun skip(count: Int = 1) {
        if (currentIndex + count > elems.size) {
            throw IndexOutOfBoundsException("Cannot skip $count elements from index $currentIndex, size is ${elems.size}")
        }
        currentIndex += count
    }

    fun consume() = skip(1)
}