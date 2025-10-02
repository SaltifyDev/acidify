package org.ntqqrev.acidify.internal.packet.oidb

import org.ntqqrev.acidify.pb.PbInt64
import org.ntqqrev.acidify.pb.PbSchema
import org.ntqqrev.acidify.pb.PbString

internal object SetGroupNameReq : PbSchema() {
    val groupCode = PbInt64[1]
    val targetName = PbString[2]
}

