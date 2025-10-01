package org.ntqqrev.acidify.internal.packet.message.elem

import org.ntqqrev.acidify.pb.*

internal object SourceMsg : PbSchema() {
    val origSeqs = PbRepeatedInt64[1]
    val senderUin = PbInt64[2]
    val time = PbInt32[3]
    val flag = PbInt32[4]
    val elems = PbRepeatedBytes[5]
    val type = PbInt32[6]
    val richMsg = PbBytes[7]
    val pbReserve = PbBytes[8]
    val srcMsg = PbBytes[9]
    val toUin = PbInt64[10]
    val troopName = PbBytes[11]

    internal object PbReserve : PbSchema() {
        val friendSequence = PbInt64[1]
    }
}