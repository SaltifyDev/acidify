package org.ntqqrev.acidify.internal.packet.oidb

import org.ntqqrev.acidify.pb.PbInt32
import org.ntqqrev.acidify.pb.PbInt64
import org.ntqqrev.acidify.pb.PbSchema

internal object SetGroupEssenceMessageReq : PbSchema() {
    val groupCode = PbInt64[1]
    val sequence = PbInt32[2]
    val random = PbInt32[3]
}

