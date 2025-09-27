package org.ntqqrev.acidify.internal.packet.message

import org.ntqqrev.acidify.pb.*

internal object RoutingHead : PbSchema() {
    val fromUin = PbInt64[1]
    val fromUid = PbString[2]
    val fromAppId = PbInt32[3]
    val fromInstId = PbInt32[4]
    val toUin = PbInt64[5]
    val toUid = PbString[6]
    val commonC2C = CommonC2C[7]
    val group = CommonGroup[8]

    internal object CommonC2C : PbSchema() {
        val c2CType = PbInt32[1]
        val serviceType = PbInt32[2]
        val sig = PbBytes[3]
        val fromTinyId = PbInt64[4]
        val toTinyId = PbInt64[5]
        val name = PbString[6]
    }

    internal object CommonGroup : PbSchema() {
        val groupCode = PbInt64[1]
        val groupType = PbInt32[2]
        val groupInfoSeq = PbInt64[3]
        val groupCard = PbString[4]
        val groupCardType = PbInt32[5]
        val groupLevel = PbInt32[6]
        val groupName = PbString[7]
        val extGroupKeyInfo = PbString[8]
        val msgFlag = PbInt32[9]
    }
}