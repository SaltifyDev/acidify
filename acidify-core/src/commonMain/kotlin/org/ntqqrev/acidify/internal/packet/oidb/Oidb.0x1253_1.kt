package org.ntqqrev.acidify.internal.packet.oidb

import org.ntqqrev.acidify.pb.PbInt32
import org.ntqqrev.acidify.pb.PbInt64
import org.ntqqrev.acidify.pb.PbSchema
import org.ntqqrev.acidify.pb.PbString

internal object SetMemberMuteReq : PbSchema() {
    val groupCode = PbInt64[1]
    val type = PbInt32[2]
    val targetUid = PbString[3]
    val duration = PbInt32[4]
}

