package org.ntqqrev.acidify.internal.packet.message

import org.ntqqrev.acidify.pb.PbInt32
import org.ntqqrev.acidify.pb.PbInt64
import org.ntqqrev.acidify.pb.PbSchema
import org.ntqqrev.acidify.pb.PbString

internal object Trans211 : PbSchema() {
    val toUin = PbInt64[1]
    val ccCmd = PbInt32[2]
    val uid = PbString[8]
}