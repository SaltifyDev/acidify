package org.ntqqrev.acidify.internal.packet.message.elem

import org.ntqqrev.acidify.pb.PbBytes
import org.ntqqrev.acidify.pb.PbSchema
import org.ntqqrev.acidify.pb.PbString

internal object Text : PbSchema() {
    val textMsg = PbString[1]
    val link = PbString[2]
    val attr6Buf = PbBytes[3]
    val attr7Buf = PbBytes[4]
    val buf = PbBytes[11]
    val pbReserve = PbBytes[12]
}