package org.ntqqrev.acidify.internal.packet.oidb

import org.ntqqrev.acidify.pb.PbInt64
import org.ntqqrev.acidify.pb.PbSchema

internal object QuitGroupReq : PbSchema() {
    val groupCode = PbInt64[1]
}

