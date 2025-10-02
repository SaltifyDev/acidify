package org.ntqqrev.acidify.internal.packet.oidb

import org.ntqqrev.acidify.pb.*

internal object SetGroupMessageReactionReq : PbSchema() {
    val groupCode = PbInt64[1]
    val sequence = PbInt32[2]
    val code = PbString[3]
    val type = PbInt32[4]
    val field6 = PbBoolean[6]
    val field7 = PbBoolean[7]
}

