package org.ntqqrev.acidify.internal.packet.message.extra

import org.ntqqrev.acidify.pb.*

internal object GroupRecall : PbSchema() {
    val operatorUid = PbString[1]
    val recallMessages = PbRepeated[RecallMessage[3]]
    val userDef = PbOptional[PbBytes[5]]
    val groupType = PbInt32[6]
    val opType = PbInt32[7]
    val tipInfo = PbOptional[TipInfo[9]]

    internal object RecallMessage : PbSchema() {
        val sequence = PbInt32[1]
        val time = PbInt32[2]
        val random = PbInt32[3]
        val type = PbInt32[4]
        val flag = PbInt32[5]
        val authorUid = PbString[6]
    }

    internal object TipInfo : PbSchema() {
        val tip = PbOptional[PbString[2]]
    }
}

