package org.ntqqrev.acidify.internal.packet.message.action

import org.ntqqrev.acidify.internal.packet.message.CommonMessage
import org.ntqqrev.acidify.pb.*

internal object PbMultiMsgTransmit : PbSchema() {
    val messages = PbRepeated[CommonMessage[1]]
    val items = PbRepeated[PbMultiMsgItem[2]]
}

internal object PbMultiMsgItem : PbSchema() {
    val fileName = PbString[1]
    val buffer = PbOptional[PbMultiMsgNew[2]]
}

internal object PbMultiMsgNew : PbSchema() {
    val msg = PbRepeated[CommonMessage[1]]
}

internal object LongMsgInterfaceReq : PbSchema() {
    val recvReq = PbOptional[LongMsgRecvReq[1]]
    val sendReq = PbOptional[LongMsgSendReq[2]]
    val attr = PbOptional[LongMsgAttr[15]]
}

internal object LongMsgInterfaceResp : PbSchema() {
    val recvResp = PbOptional[LongMsgRecvResp[1]]
    val sendResp = PbOptional[LongMsgSendResp[2]]
    val attr = PbOptional[LongMsgAttr[15]]
}

internal object LongMsgAttr : PbSchema() {
    val subCmd = PbInt32[1]      // 4
    val clientType = PbInt32[2]  // 1
    val platform = PbInt32[3]    // 7
    val proxyType = PbInt32[4]   // 0
}

internal object LongMsgPeerInfo : PbSchema() {
    val peerUid = PbString[2]
}

internal object LongMsgRecvReq : PbSchema() {
    val peerInfo = PbOptional[LongMsgPeerInfo[1]]
    val resId = PbString[2]
    val msgType = PbInt32[3]
}

internal object LongMsgSendReq : PbSchema() {
    val msgType = PbInt32[1]
    val peerInfo = PbOptional[LongMsgPeerInfo[2]]
    val groupUin = PbInt64[3]
    val payload = PbBytes[4]
}

internal object LongMsgSendResp : PbSchema() {
    val resId = PbString[3]
}

internal object LongMsgRecvResp : PbSchema() {
    val resId = PbString[3]
    val payload = PbBytes[4]
}