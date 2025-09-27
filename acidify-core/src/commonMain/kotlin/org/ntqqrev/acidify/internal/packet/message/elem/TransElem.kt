package org.ntqqrev.acidify.internal.packet.message.elem

import org.ntqqrev.acidify.pb.PbBytes
import org.ntqqrev.acidify.pb.PbInt32
import org.ntqqrev.acidify.pb.PbSchema

internal object TransElem : PbSchema() {
    val elemType = PbInt32[1]
    val elemValue = PbBytes[2]
}