package org.ntqqrev.acidify.internal.packet.message.action

import org.ntqqrev.acidify.internal.packet.message.CommonMessage
import org.ntqqrev.acidify.pb.*

internal object SsoGetGroupMsgReq : PbSchema() {
    object GroupInfo : PbSchema() {
        val groupUin = PbInt64[1]
        val startSequence = PbInt64[2]
        val endSequence = PbInt64[3]
    }

    val groupInfo = GroupInfo[1]
    val filter = PbInt32[2]
}

internal object SsoGetGroupMsgResp : PbSchema() {
    object Body : PbSchema() {
        val retcode = PbInt32[1]
        val errorMsg = PbString[2]
        val groupUin = PbInt32[3]
        val startSequence = PbInt32[4]
        val endSequence = PbInt32[5]
        val messages = PbRepeated[CommonMessage[6]]
    }

    val retcode = PbInt32[1]
    val errorMsg = PbString[2]
    val body = Body[3]
}

