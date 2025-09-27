package org.ntqqrev.acidify.internal.packet.message.elem

import org.ntqqrev.acidify.pb.PbBytes
import org.ntqqrev.acidify.pb.PbInt32
import org.ntqqrev.acidify.pb.PbSchema

internal object RichMsg : PbSchema() {
    val bytesTemplate1 = PbBytes[1]
    val serviceId = PbInt32[2]
    val bytesMsgResid = PbBytes[3]
    val rand = PbInt32[4]
    val seq = PbInt32[5]
    val flags = PbInt32[6]
}