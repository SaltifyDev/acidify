package org.ntqqrev.acidify.internal.packet.message.elem

import org.ntqqrev.acidify.pb.PbBytes
import org.ntqqrev.acidify.pb.PbInt32
import org.ntqqrev.acidify.pb.PbSchema

internal object CommonElem : PbSchema() {
    val serviceType = PbInt32[1]
    val pbElem = PbBytes[2]
    val businessType = PbInt32[3]
}