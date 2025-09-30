package org.ntqqrev.acidify.internal.packet.message.elem

import org.ntqqrev.acidify.pb.*

internal object MarketFace : PbSchema() {
    val summary = PbString[1]
    val itemType = PbInt32[2]
    val info = PbInt32[3]
    val faceId = PbBytes[4]
    val tabId = PbInt32[5]
    val subType = PbInt32[6]
    val key = PbString[7]
    val width = PbInt32[10]
    val height = PbInt32[11]
    val pbReserve = PbReserve[13]

    internal object PbReserve : PbSchema() {
        val field8 = PbInt32[8]
    }
}


