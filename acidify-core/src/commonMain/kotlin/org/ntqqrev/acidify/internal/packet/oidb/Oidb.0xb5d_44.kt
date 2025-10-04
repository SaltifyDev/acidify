package org.ntqqrev.acidify.internal.packet.oidb

import org.ntqqrev.acidify.pb.PbInt32
import org.ntqqrev.acidify.pb.PbSchema
import org.ntqqrev.acidify.pb.PbString

internal object SetFriendRequestReq : PbSchema() {
    val accept = PbInt32[1]
    val targetUid = PbString[2]
}