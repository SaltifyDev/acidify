package org.ntqqrev.acidify.internal.packet.oidb

import org.ntqqrev.acidify.pb.*

internal object SetGroupMessageReactionReq : PbSchema() {
    val groupCode = PbInt64[2]
    val sequence = PbInt32[3]
    val code = PbString[4]
    val type = PbInt32[5]
}

