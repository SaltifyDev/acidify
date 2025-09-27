package org.ntqqrev.acidify.internal.packet.message.elem

import org.ntqqrev.acidify.pb.PbBytes
import org.ntqqrev.acidify.pb.PbSchema

internal object LightAppElem : PbSchema() {
    val bytesData = PbBytes[1]
    val bytesMsgResid = PbBytes[2]
}