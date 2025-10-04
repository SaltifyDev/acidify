package org.ntqqrev.acidify.internal.packet.oidb

import org.ntqqrev.acidify.pb.*

internal object SetGroupRequestReq : PbSchema() {
    val accept = PbInt32[1]
    val body = SetGroupRequestBody[2]
}

internal object SetGroupRequestBody : PbSchema() {
    val sequence = PbInt64[1]
    val eventType = PbInt32[2]
    val groupUin = PbInt64[3]
    val message = PbString[4]
}