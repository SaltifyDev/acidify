package org.ntqqrev.acidify.internal.packet.message.extra

import org.ntqqrev.acidify.pb.PbInt32
import org.ntqqrev.acidify.pb.PbInt64
import org.ntqqrev.acidify.pb.PbSchema
import org.ntqqrev.acidify.pb.PbString

internal object TextResvAttr : PbSchema() {
    val atType = PbInt32[1] // 1 for @all, 2 for @specific
    val atMemberUin = PbInt64[2]
    val atMemberTinyid = PbInt64[3]
    val atMemberUid = PbString[4]
}