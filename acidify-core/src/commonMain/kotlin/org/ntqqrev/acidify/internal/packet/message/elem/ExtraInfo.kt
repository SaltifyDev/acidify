package org.ntqqrev.acidify.internal.packet.message.elem

import org.ntqqrev.acidify.pb.PbInt32
import org.ntqqrev.acidify.pb.PbInt64
import org.ntqqrev.acidify.pb.PbSchema
import org.ntqqrev.acidify.pb.PbString

internal object ExtraInfo : PbSchema() {
    val nick = PbString[1]
    val groupCard = PbString[2]
    val level = PbInt32[3]
    val flags = PbInt32[4]
    val groupMask = PbInt32[5]
    val msgTailId = PbInt32[6]
    val senderTitle = PbString[7]
    val apnsTips = PbString[8]
    val uin = PbInt64[9]
    val msgStateFlag = PbInt32[10]
    val apnsSoundType = PbInt32[11]
    val newGroupFlag = PbInt32[12]
}