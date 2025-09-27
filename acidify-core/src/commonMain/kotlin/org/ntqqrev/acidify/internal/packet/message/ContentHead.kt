package org.ntqqrev.acidify.internal.packet.message

import org.ntqqrev.acidify.pb.PbInt32
import org.ntqqrev.acidify.pb.PbInt64
import org.ntqqrev.acidify.pb.PbSchema

internal object ContentHead : PbSchema() {
    val type = PbInt32[1]
    val subType = PbInt32[2]
    val c2CCommand = PbInt32[3]
    val random = PbInt32[4]
    val sequence = PbInt64[5]
    val time = PbInt64[6]
    val pkgNum = PbInt32[7]
    val pkgIndex = PbInt32[8]
    val divSeq = PbInt32[9]
    val autoReply = PbInt32[10]
    val clientSequence = PbInt64[11]
    val msgUid = PbInt64[12]
}