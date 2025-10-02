package org.ntqqrev.acidify.internal.packet.oidb

import org.ntqqrev.acidify.pb.PbInt32
import org.ntqqrev.acidify.pb.PbInt64
import org.ntqqrev.acidify.pb.PbSchema

internal object SendGroupPokeReq : PbSchema() {
    val uin = PbInt64[1]
    val groupCode = PbInt64[2]
    val friendUin = PbInt64[3]
    val ext = PbInt32[11]
}

