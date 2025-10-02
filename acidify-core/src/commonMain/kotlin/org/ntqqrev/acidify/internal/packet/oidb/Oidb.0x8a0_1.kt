package org.ntqqrev.acidify.internal.packet.oidb

import org.ntqqrev.acidify.pb.*

internal object KickMemberReq : PbSchema() {
    val groupCode = PbInt64[1]
    val kickFlag = PbInt32[2]
    val targetUid = PbString[3]
    val rejectAddRequest = PbBoolean[4]
    val reason = PbString[5]
}

