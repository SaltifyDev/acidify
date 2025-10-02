package org.ntqqrev.acidify.internal.packet.oidb

import org.ntqqrev.acidify.pb.PbBoolean
import org.ntqqrev.acidify.pb.PbInt64
import org.ntqqrev.acidify.pb.PbSchema
import org.ntqqrev.acidify.pb.PbString

internal object SetMemberAdminReq : PbSchema() {
    val groupCode = PbInt64[1]
    val targetUid = PbString[2]
    val isAdmin = PbBoolean[3]
}

