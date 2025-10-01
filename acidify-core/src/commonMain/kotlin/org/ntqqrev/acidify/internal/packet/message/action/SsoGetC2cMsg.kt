package org.ntqqrev.acidify.internal.packet.message.action

import org.ntqqrev.acidify.internal.packet.message.CommonMessage
import org.ntqqrev.acidify.pb.*

internal object SsoGetC2cMsgReq : PbSchema() {
    val peerUid = PbString[2]
    val startSequence = PbInt64[3]
    val endSequence = PbInt64[4]
}

internal object SsoGetC2cMsgResp : PbSchema() {
    val retcode = PbInt32[1]
    val errorMsg = PbString[2]
    val messages = PbRepeated[CommonMessage[7]]
}

