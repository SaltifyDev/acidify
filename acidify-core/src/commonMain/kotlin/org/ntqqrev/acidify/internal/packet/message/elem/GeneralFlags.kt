package org.ntqqrev.acidify.internal.packet.message.elem

import org.ntqqrev.acidify.pb.*

internal object GeneralFlags : PbSchema() {
    val bubbleDiyTextId = PbInt32[1]
    val groupFlagNew = PbInt32[2]
    val uin = PbInt64[3]
    val rpId = PbBytes[4]
    val prpFold = PbInt32[5]
    val longTextFlag = PbInt32[6]
    val longTextResId = PbString[7]
    val groupType = PbInt32[8]
    val toUinFlag = PbInt32[9]
    val glamourLevel = PbInt32[10]
    val memberLevel = PbInt32[11]
    val groupRankSeq = PbInt64[12]
    val olympicTorch = PbInt32[13]
    val babyQGuideMsgCookie = PbBytes[14]
    val uin32ExpertFlag = PbInt32[15]
    val bubbleSubId = PbInt32[16]
    val pendantId = PbInt64[17]
    val rpIndex = PbBytes[18]
    val pbReserve = PbBytes[19]
}