package org.ntqqrev.acidify.internal.packet.oidb

import org.ntqqrev.acidify.pb.PbInt32
import org.ntqqrev.acidify.pb.PbSchema
import org.ntqqrev.acidify.pb.PbString

internal object ProfileLikeReq : PbSchema() {
    val targetUid = PbString[11]
    val field2 = PbInt32[12]
    val field3 = PbInt32[13]
}