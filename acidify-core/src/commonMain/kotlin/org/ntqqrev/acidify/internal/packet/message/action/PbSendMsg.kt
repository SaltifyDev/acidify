package org.ntqqrev.acidify.internal.packet.message.action

import org.ntqqrev.acidify.internal.packet.message.MessageBody
import org.ntqqrev.acidify.internal.packet.message.SendContentHead
import org.ntqqrev.acidify.internal.packet.message.SendRoutingHead
import org.ntqqrev.acidify.pb.*

internal object PbSendMsgReq : PbSchema() {
    val routingHead = SendRoutingHead[1]
    val contentHead = SendContentHead[2]
    val messageBody = MessageBody[3]
    val clientSequence = PbInt64[4]
    val random = PbInt32[5]
}

internal object PbSendMsgResp : PbSchema() {
    val result = PbInt32[1]
    val errMsg = PbString[2]
    val sendTime = PbInt64[3]
    val msgInfoFlag = PbInt32[10]
    val sequence = PbInt64[11]
    val clientSequence = PbInt64[14]
}