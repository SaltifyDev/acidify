package org.ntqqrev.acidify.internal.packet.message.extra

import org.ntqqrev.acidify.pb.PbInt64
import org.ntqqrev.acidify.pb.PbSchema
import org.ntqqrev.acidify.pb.PbString

internal object GroupJoinRequest : PbSchema() {
    val groupUin = PbInt64[1]
    val memberUid = PbString[3]
}

