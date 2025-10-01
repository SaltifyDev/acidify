package org.ntqqrev.acidify.internal.packet.message

import org.ntqqrev.acidify.pb.PbInt32
import org.ntqqrev.acidify.pb.PbSchema

internal object SendContentHead : PbSchema() {
    val pkgNum = PbInt32[1]
    val pkgIndex = PbInt32[2]
    val divSeq = PbInt32[3]
    val autoReply = PbInt32[4]
}