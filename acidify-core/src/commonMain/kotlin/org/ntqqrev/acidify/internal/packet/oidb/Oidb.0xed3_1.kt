package org.ntqqrev.acidify.internal.packet.oidb

import org.ntqqrev.acidify.pb.PbInt32
import org.ntqqrev.acidify.pb.PbInt64
import org.ntqqrev.acidify.pb.PbSchema

internal object PokeReq : PbSchema() {
    val targetUin = PbInt64[1]
    val groupUin = PbInt64[2]
    val friendUin = PbInt64[5]
    val ext = PbInt32[6]
}